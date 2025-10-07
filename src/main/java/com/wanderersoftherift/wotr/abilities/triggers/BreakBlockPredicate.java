package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;

public class BreakBlockPredicate implements TriggerPredicate<BreakBlockTrigger> {
    private static final BreakBlockPredicate INSTANCE = new BreakBlockPredicate();
    public static final MapCodec<BreakBlockPredicate> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public Holder<TrackableTrigger.TriggerType<?>> type() {
        return WotrTrackedAbilityTriggers.BREAK_BLOCK.getDelegate();
    }

    @Override
    public boolean canBeHandledByClient() {
        return false;
    }

    @Override
    public boolean test(BreakBlockTrigger breakBlockTrigger) {
        return true;
    }
}
