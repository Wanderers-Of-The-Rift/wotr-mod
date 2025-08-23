package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.Nullable;

public class AbilityTracker {

    public static final IAttachmentSerializer<Tag, AbilityTracker> SERIALIZER = new AttachmentSerializerFromDataCodec<>(
            Data.CODEC, AbilityTracker::new, AbilityTracker::data);

    private final Multimap<Holder<TrackedAbilityTrigger.Type<?>>, TrackedAbility> abilities = ArrayListMultimap
            .create();
    private final IAttachmentHolder holder;

    public AbilityTracker(IAttachmentHolder holder) {
        this(holder, null);
    }

    public AbilityTracker(IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
    }

    public static AbilityTracker forEntity(Entity entity) {
        return entity.getData(WotrAttachments.ABILITY_TRACKER.get());
    }

    public static AbilityTracker forEntityNullable(Entity entity) {
        var type = WotrAttachments.ABILITY_TRACKER.get();
        if (!entity.hasData(type)) {
            return null;
        }
        return entity.getData(type);
    }

    public static IAttachmentSerializer<Tag, AbilityTracker> getSerializer() {
        return SERIALIZER;
    }

    private Data data() {
        return null;
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

    public boolean hasAbilitiesOnTrigger(Holder<TrackedAbilityTrigger.Type<?>> activation) {
        return !abilities.get(activation).isEmpty();
    }

    public void registerAbility(AbilityModifier abilityModifier, @Nullable WotrEquipmentSlot slot) {
        registerAbility(abilityModifier.trigger(), abilityModifier.providedAbility(), slot);
    }

    public void registerAbility(
            Holder<TrackedAbilityTrigger.Type<?>> trigger,
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

    public void unregisterAbility(AbilityModifier abilityModifier, @Nullable WotrEquipmentSlot slot) {
        unregisterAbility(abilityModifier.trigger(), abilityModifier.providedAbility(), slot);
    }

    public void unregisterAbility(
            Holder<TrackedAbilityTrigger.Type<?>> trigger,
            Holder<Ability> abilityHolder,
            @Nullable WotrEquipmentSlot slot) {
        abilities.get(trigger)
                .removeIf(trackedAbility -> trackedAbility.ability.equals(abilityHolder)
                        && slot.equals(trackedAbility.slot));
    }

    record TrackedAbility(WotrEquipmentSlot slot, Holder<Ability> ability) {
    }

    record Data() {
        private static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.point((Data) null));
    }
}
