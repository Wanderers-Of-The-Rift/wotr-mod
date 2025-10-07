package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;

public record KillPredicate() implements TriggerPredicate<KillTrigger> {
    public static final MapCodec<KillPredicate> CODEC = MapCodec.unit(new KillPredicate());

    @Override
    public Holder<TrackableTrigger.TriggerType<?>> type() {
        return WotrTrackedAbilityTriggers.KILL.getDelegate();
    }

    @Override
    public boolean canBeHandledByClient() {
        return true;
    }

    @Override
    public boolean test(KillTrigger trigger) {
        return true;
    }
}
