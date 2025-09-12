package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.ability.AbilityModifier;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.Optional;

public class AbilityTracker {
    private final Multimap<Holder<TrackedAbilityTrigger.TriggerType<?>>, Triggerable> abilities = ArrayListMultimap
            .create();
    private final IAttachmentHolder holder;

    public AbilityTracker(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public static AbilityTracker forEntity(Entity entity) {
        return entity.getData(WotrAttachments.ABILITY_TRACKER.get());
    }

    public static Optional<? extends AbilityTracker> forEntityNullable(Entity entity) {
        return entity.getExistingData(WotrAttachments.ABILITY_TRACKER.get());
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
        var triggerables = new ArrayList<>(abilities.get(typeHolder));
        for (Triggerable tracked : triggerables) {
            result |= tracked.trigger(entity);
        }
        return result;
    }

    public boolean hasAbilitiesOnTrigger(Holder<TrackedAbilityTrigger.TriggerType<?>> activation) {
        return !abilities.get(activation).isEmpty();
    }

    public void registerAbility(AbilityModifier abilityModifier, AbilitySource source) {
        registerAbility(abilityModifier.trigger(), abilityModifier.providedAbility(), source);
    }

    public void registerAbility(
            Holder<TrackedAbilityTrigger.TriggerType<?>> trigger,
            Holder<Ability> abilityHolder,
            AbilitySource source) {
        registerTriggerable(trigger, new TrackedAbility(source, abilityHolder));
    }

    public void registerTriggerable(Holder<TrackedAbilityTrigger.TriggerType<?>> trigger, Triggerable triggerable) {
        abilities.put(trigger, triggerable);
        if (holder instanceof Entity livingEntity && livingEntity.level() instanceof ServerLevel serverLevel) {
            var registryTypeSupplier = trigger.value().registry();
            if (registryTypeSupplier != null) {
                serverLevel.getData(registryTypeSupplier.get()).add(livingEntity);
            }
        }
    }

    public void unregisterAbility(AbilityModifier abilityModifier, AbilitySource source) {
        unregisterAbility(abilityModifier.trigger(), abilityModifier.providedAbility(), source);
    }

    public void unregisterAbility(
            Holder<TrackedAbilityTrigger.TriggerType<?>> trigger,
            Holder<Ability> abilityHolder,
            AbilitySource source) {
        unregisterTriggerable(trigger, new TrackedAbility(source, abilityHolder));
    }

    public void unregisterTriggerable(Holder<TrackedAbilityTrigger.TriggerType<?>> trigger, Triggerable triggerable) {
        abilities.get(trigger).removeIf(trackedAbility -> trackedAbility.equals(triggerable));
    }

    record TrackedAbility(AbilitySource source, Holder<Ability> ability) implements Triggerable {
        @Override
        public boolean trigger(LivingEntity holder) {
            return holder.getData(WotrAttachments.ONGOING_ABILITIES)
                    .activate(source, source.getItem((LivingEntity) holder), ability);
        }
    }

    public interface Triggerable {

        boolean trigger(LivingEntity holder);
    }

}
