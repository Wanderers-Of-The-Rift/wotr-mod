package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.TrackableTrigger;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.ability.TriggerableAbilityModifier;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.Optional;

public class TriggerTracker {
    private final Multimap<Holder<TrackableTrigger.TriggerType<?>>, Triggerable> abilities = ArrayListMultimap.create();
    private final IAttachmentHolder holder;

    public TriggerTracker(IAttachmentHolder holder) {
        this.holder = holder;
    }

    public static TriggerTracker forEntity(Entity entity) {
        return entity.getData(WotrAttachments.TRIGGER_TRACKER.get());
    }

    public static Optional<? extends TriggerTracker> forEntityNullable(Entity entity) {
        return entity.getExistingData(WotrAttachments.TRIGGER_TRACKER.get());
    }

    private Level getLevel() {
        if (holder instanceof Entity entity) {
            return entity.level();
        }
        return null;
    }

    public boolean trigger(TrackableTrigger activation) {
        if (!(holder instanceof LivingEntity entity)) {
            return false;
        }
        var result = false;
        var typeHolder = getLevel().registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.TRACKED_ABILITY_TRIGGERS)
                .wrapAsHolder(activation.type());
        var triggerables = new ArrayList<>(abilities.get(typeHolder));
        for (Triggerable tracked : triggerables) {
            result |= tracked.trigger(entity, activation);
        }
        return result;
    }

    public boolean hasListenersOnTrigger(Holder<TrackableTrigger.TriggerType<?>> activation) {
        return !abilities.get(activation).isEmpty();
    }

    public void registerAbilityTrigger(TriggerableAbilityModifier abilityModifier, AbilitySource source) {
        registerAbilityTrigger(abilityModifier.trigger(), abilityModifier.providedAbility(), source);
    }

    public void registerAbilityTrigger(
            Holder<TrackableTrigger.TriggerType<?>> trigger,
            Holder<Ability> abilityHolder,
            AbilitySource source) {
        registerTriggerable(trigger, new TriggerableAbility(source, abilityHolder));
    }

    public void registerTriggerable(Holder<TrackableTrigger.TriggerType<?>> trigger, Triggerable triggerable) {
        abilities.put(trigger, triggerable);
        if (!(holder instanceof Entity livingEntity) || !(livingEntity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        var registryTypeSupplier = trigger.value().registry();
        if (registryTypeSupplier != null) {
            serverLevel.getData(registryTypeSupplier.get()).add(livingEntity);
        }
    }

    public void unregisterAbilityTrigger(TriggerableAbilityModifier abilityModifier, AbilitySource source) {
        unregisterAbilityTrigger(abilityModifier.trigger(), abilityModifier.providedAbility(), source);
    }

    public void unregisterAbilityTrigger(
            Holder<TrackableTrigger.TriggerType<?>> trigger,
            Holder<Ability> abilityHolder,
            AbilitySource source) {
        unregisterTriggerable(trigger, new TriggerableAbility(source, abilityHolder));
    }

    public void unregisterTriggerable(Holder<TrackableTrigger.TriggerType<?>> trigger, Triggerable triggerable) {
        abilities.get(trigger).removeIf(trackedAbility -> trackedAbility.equals(triggerable));
    }

    public interface Triggerable {
        boolean trigger(LivingEntity holder, TrackableTrigger activation);
    }

    record TriggerableAbility(AbilitySource source, Holder<Ability> ability) implements Triggerable {
        @Override
        public boolean trigger(LivingEntity holder, TrackableTrigger activation) {
            return holder.getData(WotrAttachments.ONGOING_ABILITIES).activate(source, source.getItem(holder), ability);
        }

    }

}
