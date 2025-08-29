package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class OngoingAbilities {

    private static final AttachmentSerializerFromDataCodec<Data, OngoingAbilities> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, OngoingAbilities::new, OngoingAbilities::getData);

    private final IAttachmentHolder holder;
    private final List<ActiveAbility> activeAbilities;

    public OngoingAbilities(IAttachmentHolder holder) {
        this(holder, new Data(List.of()));
    }

    private OngoingAbilities(IAttachmentHolder holder, Data data) {
        this.holder = holder;
        activeAbilities = new ArrayList<>(data.activeAbilities);
        if (holder instanceof Entity entity) {
            var registry = entity.level().getData(WotrAttachments.ONGOING_ABILITY_ENTITY_REGISTRY);
            registry.add(entity);
        }
    }

    public static IAttachmentSerializer<Tag, OngoingAbilities> getSerializer() {
        return SERIALIZER;
    }

    public boolean activate(MainAbilitySource source) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        ItemStack abilityItem = source.getItem(entity);
        Holder<Ability> ability = source.getMainAbility(entity);
        if (ability == null) {
            return false;
        }
        return activate(source, abilityItem, ability);
    }

    public boolean activate(AbilitySource source, ItemStack abilityItem, Holder<Ability> ability) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        if (entity.level().isClientSide()) {
            return clientsideActivate(entity, ability, abilityItem, source);
        } else {
            return serversideActivate(entity, ability, abilityItem, source);
        }
    }

    private boolean clientsideActivate(
            LivingEntity entity,
            Holder<Ability> ability,
            ItemStack abilityItem,
            AbilitySource source) {
        AbilityContext context = new AbilityContext(UUID.randomUUID(), ability, entity, abilityItem, source,
                entity.level(), source.upgrades(entity));
        try (var ignore = context.enableTemporaryUpgradeModifiers()) {
            if (ability.value().canActivate(context)) {
                ability.value().clientActivate(context);
                return true;
            }
        }
        return false;
    }

    private boolean serversideActivate(
            LivingEntity entity,
            Holder<Ability> ability,
            ItemStack abilityItem,
            AbilitySource source) {
        Optional<UUID> existingId = activeAbilities.stream()
                .filter(x -> x.matches(ability, source))
                .map(ActiveAbility::id)
                .findFirst();
        boolean existing = existingId.isPresent();
        UUID id = existingId.orElseGet(UUID::randomUUID);
        var upgrades = source.upgrades(entity);
        AbilityContext context = new AbilityContext(id, ability, entity, abilityItem, source, entity.level(), upgrades);
        try (var ignore = context.enableTemporaryUpgradeModifiers()) {
            if (!ability.value().canActivate(context)) {
                return false;
            }
            boolean finished = ability.value().activate(context);
            if (finished && existing) {
                activeAbilities.removeIf(x -> x.id.equals(id));
            } else if (!finished && !existing) {
                activeAbilities.add(new ActiveAbility(context));
            }
        }
        return true;
    }

    public void tick() {
        if (!(holder instanceof LivingEntity attachedTo)) {
            return;
        }
        for (ActiveAbility instance : ImmutableList.copyOf(activeAbilities)) {
            instance.age++;
            AbilityContext context = new AbilityContext(instance.id, instance.ability, attachedTo, instance.abilityItem,
                    instance.source, attachedTo.level(), instance.upgrades);
            try (var ignore = context.enableTemporaryUpgradeModifiers()) {
                if (instance.ability.value().tick(context, instance.age)) {
                    activeAbilities.remove(instance);
                }
            }
        }
    }

    private Data getData() {
        return new Data(activeAbilities);
    }

    public boolean isEmpty() {
        return activeAbilities.isEmpty();
    }

    private static final class ActiveAbility {

        private static final Codec<ActiveAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(ActiveAbility::id),
                Ability.CODEC.fieldOf("ability").forGetter(ActiveAbility::ability),
                AbilitySource.DIRECT_CODEC.fieldOf("item_slot").forGetter(ActiveAbility::source),
                ItemStack.OPTIONAL_CODEC.fieldOf("ability_item").forGetter(ActiveAbility::abilityItem),
                Codec.LONG.fieldOf("age").forGetter(ActiveAbility::age),
                AbilityUpgradePool.CODEC.fieldOf("upgrades").forGetter(ActiveAbility::upgrades)
        ).apply(instance, ActiveAbility::new));

        private final UUID id;
        private final Holder<Ability> ability;
        private final AbilitySource source;
        private final ItemStack abilityItem;
        private final AbilityUpgradePool upgrades;
        private long age;

        private ActiveAbility(AbilityContext context) {
            this(context.instanceId(), context.ability(), context.source(), context.abilityItem(), 0,
                    context.upgrades());
        }

        private ActiveAbility(UUID id, Holder<Ability> ability, AbilitySource source, ItemStack abilityItem, long age,
                AbilityUpgradePool upgrades) {
            this.id = id;
            this.ability = ability;
            this.source = source;
            this.abilityItem = abilityItem;
            this.age = age;
            this.upgrades = upgrades;
        }

        public boolean matches(Holder<Ability> ability, AbilitySource source) {
            return this.ability.equals(ability) && Objects.equals(source, this.source);
        }

        public UUID id() {
            return id;
        }

        public Holder<Ability> ability() {
            return ability;
        }

        public AbilitySource source() {
            return source;
        }

        public ItemStack abilityItem() {
            return abilityItem;
        }

        public long age() {
            return age;
        }

        public AbilityUpgradePool upgrades() {
            return upgrades;
        }
    }

    private record Data(List<ActiveAbility> activeAbilities) {
        static final Codec<Data> CODEC = ActiveAbility.CODEC.listOf().xmap(Data::new, Data::activeAbilities);
    }
}
