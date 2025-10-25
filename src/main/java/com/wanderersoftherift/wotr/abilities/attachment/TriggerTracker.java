package com.wanderersoftherift.wotr.abilities.attachment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.triggers.TrackableTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TriggerPredicate;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.ability.TriggerableAbilityModifier;
import com.wanderersoftherift.wotr.network.ability.AbilityTriggerablePayload;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class TriggerTracker {
    private final Multimap<Holder<TrackableTrigger.TriggerType<?>>, Triggerable> triggerables = ArrayListMultimap
            .create();
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
                .lookupOrThrow(WotrRegistries.Keys.TRACKABLE_TRIGGERS)
                .wrapAsHolder(activation.type());
        var triggerables = new ArrayList<>(this.triggerables.get(typeHolder));
        for (Triggerable tracked : triggerables) {
            if (((Predicate<TrackableTrigger>) tracked.predicate()).test(activation)) {
                result |= tracked.trigger(entity, activation);
            }
        }
        return result;
    }

    public boolean hasListenersOnTrigger(Holder<TrackableTrigger.TriggerType<?>> activation) {
        return !triggerables.get(activation).isEmpty();
    }

    public void registerAbilityTrigger(TriggerableAbilityModifier abilityModifier, AbilitySource source) {
        registerAbilityTrigger(abilityModifier.trigger(), abilityModifier.providedAbility(), source);
    }

    public void registerAbilityTrigger(
            TriggerPredicate<?> trigger,
            Holder<Ability> abilityHolder,
            AbilitySource source) {
        registerTriggerable(trigger.type(), new TriggerableAbility(source, abilityHolder, trigger));
    }

    public void registerTriggerable(Holder<TrackableTrigger.TriggerType<?>> trigger, Triggerable triggerable) {
        triggerables.put(trigger, triggerable);
        if (holder instanceof ServerPlayer player && triggerable.predicate().canBeHandledByClient()) {
            triggerable.sendRegister(player);
        }
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
            TriggerPredicate<?> trigger,
            Holder<Ability> abilityHolder,
            AbilitySource source) {
        unregisterTriggerable(trigger.type(), new TriggerableAbility(source, abilityHolder, trigger));
    }

    public void unregisterTriggerable(Holder<TrackableTrigger.TriggerType<?>> trigger, Triggerable triggerable) {
        if (holder instanceof ServerPlayer player && triggerable.predicate().canBeHandledByClient()) {
            triggerable.sendUnregister(player);
        }
        triggerables.get(trigger).removeIf(trackedAbility -> trackedAbility.equals(triggerable));
    }

    public interface Triggerable {

        TriggerPredicate<?> predicate();

        boolean trigger(LivingEntity holder, TrackableTrigger activation);

        void sendUnregister(ServerPlayer player);

        void sendRegister(ServerPlayer player);
    }

    record TriggerableAbility(AbilitySource source, Holder<Ability> ability, TriggerPredicate<?> predicate)
            implements Triggerable {
        @Override
        public boolean trigger(LivingEntity holder, TrackableTrigger activation) {
            if (holder.getServer() == null || predicate().type().value() != activation.type()) {
                return false;
            }
            return holder.getData(WotrAttachments.ONGOING_ABILITIES)
                    .activate(source, ability, activation::addComponents);
        }

        // todo https://github.com/Wanderers-Of-The-Rift/wotr-mod/issues/180
        @Override
        public void sendUnregister(ServerPlayer player) {
            if (player.connection != null) {
                player.connection.send(new AbilityTriggerablePayload(source, ability, predicate, false));
            }
        }

        @Override
        public void sendRegister(ServerPlayer player) {
            if (player.connection != null) {
                player.connection.send(new AbilityTriggerablePayload(source, ability, predicate, true));
            }
        }

    }

}
