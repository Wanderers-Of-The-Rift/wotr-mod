package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import com.wanderersoftherift.wotr.serialization.VoidCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class AbilityTracker {
    public static final IAttachmentSerializer<Tag, AbilityTracker> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            VoidCodec.INSTANCE, AbilityTracker::new, (unused) -> null);

    private final Multimap<Holder<TrackedAbilityTrigger.TriggerType<?>>, TrackedAbility> abilities = ArrayListMultimap
            .create();
    private final IAttachmentHolder holder;

    public AbilityTracker(IAttachmentHolder holder) {
        this(holder, null);
    }

    public AbilityTracker(IAttachmentHolder holder, @Nullable Void data) {
        this.holder = holder;
    }

    public static AbilityTracker forEntity(Entity entity) {
        return entity.getData(WotrAttachments.ABILITY_TRACKER.get());
    }

    public static Optional<? extends AbilityTracker> forEntityNullable(Entity entity) {
        return entity.getExistingData(WotrAttachments.ABILITY_TRACKER.get());
    }

    public static IAttachmentSerializer<Tag, AbilityTracker> getSerializer() {
        return SERIALIZER;
    }

    private Level getLevel() {
        if (holder instanceof Entity entity) {
            return entity.level();
        }
        return null;
    }

    public boolean triggerAbilities(TrackedAbilityTrigger activation) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        var result = false;
        var typeHolder = getLevel().registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.TRACKED_ABILITY_TRIGGERS)
                .wrapAsHolder(activation.type());
        for (var tracked : abilities.get(typeHolder)) {
            result |= holder.getData(WotrAttachments.ONGOING_ABILITIES)
                    .activate(tracked.slot, tracked.slot.getContent(entity), tracked.ability);
        }
        return result;
    }

    public boolean hasAbilitiesOnTrigger(Holder<TrackedAbilityTrigger.TriggerType<?>> activation) {
        return !abilities.get(activation).isEmpty();
    }

    public void registerAbility(AbilityModifier abilityModifier, WotrEquipmentSlot slot) {
        registerAbility(abilityModifier.trigger(), abilityModifier.providedAbility(), slot);
    }

    public void registerAbility(
            Holder<TrackedAbilityTrigger.TriggerType<?>> trigger,
            Holder<Ability> abilityHolder,
            WotrEquipmentSlot slot) {
        abilities.put(trigger, new TrackedAbility(slot, abilityHolder));
        if (holder instanceof Entity livingEntity && livingEntity.level() instanceof ServerLevel serverLevel) {
            var registryTypeSupplier = trigger.value().registry();
            if (registryTypeSupplier != null) {
                serverLevel.getData(registryTypeSupplier.get()).add(livingEntity);
            }
        }
    }

    public void unregisterAbility(AbilityModifier abilityModifier, WotrEquipmentSlot slot) {
        unregisterAbility(abilityModifier.trigger(), abilityModifier.providedAbility(), slot);
    }

    public void unregisterAbility(
            Holder<TrackedAbilityTrigger.TriggerType<?>> trigger,
            Holder<Ability> abilityHolder,
            WotrEquipmentSlot slot) {
        abilities.get(trigger)
                .removeIf(trackedAbility -> Objects.equals(trackedAbility.ability, abilityHolder)
                        && Objects.equals(slot, trackedAbility.slot));
    }

    record TrackedAbility(WotrEquipmentSlot slot, Holder<Ability> ability) {
    }

}
