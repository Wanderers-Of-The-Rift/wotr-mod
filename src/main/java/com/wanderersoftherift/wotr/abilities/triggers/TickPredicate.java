package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;

public record TickPredicate() implements TriggerPredicate<TickTrigger> {
    public static final MapCodec<TickPredicate> CODEC = MapCodec.unit(new TickPredicate());

    @Override
    public Holder<TrackableTrigger.TriggerType<?>> type() {
        return WotrTrackedAbilityTriggers.TICK_TRIGGER.getDelegate();
    }

    @Override
    public boolean canBeHandledByClient() {
        return true;
    }

    @Override
    public boolean test(TickTrigger trigger) {
        return true;
    }
}
