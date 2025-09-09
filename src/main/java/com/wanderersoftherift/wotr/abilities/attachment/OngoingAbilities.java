package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.EnhancingModifierInstance;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.sources.MainAbilitySource;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

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
        Holder<Ability> ability = source.getAbility(entity);
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
        var enhancements = AbilityEnhancements.forEntity(entity).modifiers(ability);
        AbilityContext context = new AbilityContext(UUID.randomUUID(), ability, entity, abilityItem, source,
                entity.level(), source.upgrades(entity), enhancements, Collections.emptySet() /* todo */);
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
        interruptChannelledAbilities();
        Optional<UUID> existingId = activeAbilities.stream()
                .filter(x -> x.matches(ability, source))
                .map(ActiveAbility::id)
                .findFirst();
        boolean existing = existingId.isPresent();
        UUID id = existingId.orElseGet(UUID::randomUUID);
        var upgrades = source.upgrades(entity);
        var enhancements = AbilityEnhancements.forEntity(entity).modifiers(ability);
        AbilityContext context = new AbilityContext(id, ability, entity, abilityItem, source, entity.level(), upgrades,
                enhancements, Collections.emptySet() /* todo */);
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
            AbilityContext context = instance.createContext(attachedTo);
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

    public void slotChanged(@NotNull WotrEquipmentSlot slot, ItemStack from, ItemStack to) {
        deactivateIf(x -> slot.equals(x.source.getLinkedSlot()));
    }

    public void interruptChannelledAbilities() {
        deactivateIf(x -> x.ability.value().isChannelled());
    }

    private void deactivateIf(Predicate<ActiveAbility> predicate) {
        if (!(holder instanceof LivingEntity attachedTo)) {
            return;
        }
        List<ActiveAbility> channelled = activeAbilities.stream().filter(predicate).toList();
        channelled.forEach(instance -> {
            AbilityContext abilityContext = instance.createContext(attachedTo);
            try (var ignored = abilityContext.enableTemporaryUpgradeModifiers()) {
                instance.ability.value().deactivate(abilityContext);
            }
        });
        activeAbilities.removeAll(channelled);
    }

    private static final class ActiveAbility {

        private static final Codec<ActiveAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(ActiveAbility::id),
                Ability.CODEC.fieldOf("ability").forGetter(ActiveAbility::ability),
                AbilitySource.DIRECT_CODEC.fieldOf("item_slot").forGetter(ActiveAbility::source),
                ItemStack.OPTIONAL_CODEC.fieldOf("ability_item").forGetter(ActiveAbility::abilityItem),
                Codec.LONG.fieldOf("age").forGetter(ActiveAbility::age),
                AbilityUpgrade.REGISTRY_CODEC.listOf()
                        .optionalFieldOf("upgrades", List.of())
                        .forGetter(ActiveAbility::upgrades),
                EnhancingModifierInstance.CODEC.listOf().fieldOf("enhancements").forGetter(ActiveAbility::enhancements),
                ResourceLocation.CODEC.listOf().fieldOf("conditions").forGetter(ActiveAbility::conditions)
        ).apply(instance, ActiveAbility::new));

        private final UUID id;
        private final Holder<Ability> ability;
        private final AbilitySource source;
        private final ItemStack abilityItem;
        private final List<Holder<AbilityUpgrade>> upgrades;
        private final List<EnhancingModifierInstance> enhancements;
        private final List<ResourceLocation> conditions;
        private long age;

        ActiveAbility(AbilityContext context) {
            this(context.instanceId(), context.ability(), context.source(), context.abilityItem(), 0,
                    context.upgrades(), context.enhancements(), List.copyOf(context.conditions()));
        }

        ActiveAbility(UUID id, Holder<Ability> ability, AbilitySource source, ItemStack abilityItem, long age,
                List<Holder<AbilityUpgrade>> upgrades, List<EnhancingModifierInstance> enhancements,
                List<ResourceLocation> conditions) {
            this.id = id;
            this.ability = ability;
            this.source = source;
            this.abilityItem = abilityItem;
            this.age = age;
            this.upgrades = upgrades;
            this.enhancements = enhancements;
            this.conditions = conditions;
        }

        boolean matches(Holder<Ability> ability, AbilitySource source) {
            return this.ability.equals(ability) && Objects.equals(source, this.source);
        }

        AbilityContext createContext(LivingEntity owner) {
            return new AbilityContext(id, ability, owner, abilityItem, source, owner.level(), upgrades, enhancements,
                    new HashSet<>(conditions));
        }

        UUID id() {
            return id;
        }

        Holder<Ability> ability() {
            return ability;
        }

        AbilitySource source() {
            return source;
        }

        ItemStack abilityItem() {
            return abilityItem;
        }

        List<ResourceLocation> conditions() {
            return conditions;
        }

        long age() {
            return age;
        }

        List<Holder<AbilityUpgrade>> upgrades() {
            return upgrades;
        }

        public List<EnhancingModifierInstance> enhancements() {
            return enhancements;
        }
    }

    private record Data(List<ActiveAbility> activeAbilities) {
        static final Codec<Data> CODEC = ActiveAbility.CODEC.listOf().xmap(Data::new, Data::activeAbilities);
    }
}
