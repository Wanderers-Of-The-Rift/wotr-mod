package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.item.ability.ActivatableAbility;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import com.wanderersoftherift.wotr.util.EntityAttachmentRegistry;
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
            EntityAttachmentRegistry registry = entity.level().getData(WotrAttachments.ENTITY_ATTACHMENT_REGISTRY);
            registry.add(WotrAttachments.ONGOING_ABILITIES, entity);
        }
    }

    public static IAttachmentSerializer<Tag, OngoingAbilities> getSerializer() {
        return SERIALIZER;
    }

    public boolean activate(WotrEquipmentSlot slot) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        ItemStack abilityItem = slot.getContent(entity);
        ActivatableAbility abilityComponent = abilityItem.get(WotrDataComponentType.ABILITY);
        if (abilityComponent == null) {
            return false;
        }
        Holder<Ability> ability = abilityComponent.ability();

        if (entity.level().isClientSide()) {
            return clientsideActivate(entity, ability, abilityItem, slot);
        } else {
            return serversideActivate(entity, ability, abilityItem, slot);
        }
    }

    private boolean clientsideActivate(
            LivingEntity entity,
            Holder<Ability> ability,
            ItemStack abilityItem,
            WotrEquipmentSlot slot) {
        AbilityContext context = new AbilityContext(UUID.randomUUID(), ability, entity, abilityItem, slot,
                entity.level());
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
            WotrEquipmentSlot slot) {
        Optional<UUID> existingId = activeAbilities.stream()
                .filter(x -> x.matches(ability, slot))
                .map(ActiveAbility::id)
                .findFirst();
        boolean existing = existingId.isPresent();
        UUID id = existingId.orElseGet(UUID::randomUUID);
        AbilityContext context = new AbilityContext(id, ability, entity, abilityItem, slot, entity.level());
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
                    instance.slot.orElse(null), attachedTo.level());
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
                WotrEquipmentSlot.DIRECT_CODEC.optionalFieldOf("item_slot").forGetter(ActiveAbility::slot),
                ItemStack.OPTIONAL_CODEC.fieldOf("ability_item").forGetter(ActiveAbility::abilityItem),
                Codec.LONG.fieldOf("age").forGetter(ActiveAbility::age)
        ).apply(instance, ActiveAbility::new));

        private final UUID id;
        private final Holder<Ability> ability;
        private final Optional<WotrEquipmentSlot> slot;
        private final ItemStack abilityItem;
        private long age;

        private ActiveAbility(AbilityContext context) {
            this(context.instanceId(), context.ability(), Optional.ofNullable(context.slot()), context.abilityItem(),
                    0);
        }

        private ActiveAbility(UUID id, Holder<Ability> ability, Optional<WotrEquipmentSlot> slot, ItemStack abilityItem,
                long age) {
            this.id = id;
            this.ability = ability;
            this.slot = slot;
            this.abilityItem = abilityItem;
            this.age = age;
        }

        public boolean matches(Holder<Ability> ability, WotrEquipmentSlot slot) {
            return this.ability.equals(ability) && Objects.equals(slot, this.slot.orElse(null));
        }

        public UUID id() {
            return id;
        }

        public Holder<Ability> ability() {
            return ability;
        }

        public Optional<WotrEquipmentSlot> slot() {
            return slot;
        }

        public ItemStack abilityItem() {
            return abilityItem;
        }

        public long age() {
            return age;
        }
    }

    private record Data(List<ActiveAbility> activeAbilities) {
        static final Codec<Data> CODEC = ActiveAbility.CODEC.listOf().xmap(Data::new, Data::activeAbilities);
    }
}
