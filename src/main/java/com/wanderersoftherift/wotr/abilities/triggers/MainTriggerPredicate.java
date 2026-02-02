package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import net.minecraft.core.Holder;

public class MainTriggerPredicate implements TriggerPredicate<MainAttackTrigger> {
    public static final MainTriggerPredicate INSTANCE = new MainTriggerPredicate();

    public static final MapCodec<MainTriggerPredicate> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public Holder<TrackableTrigger.TriggerType<?>> type() {
        return WotrTrackedAbilityTriggers.MAIN_ATTACK;
    }

    @Override
    public boolean canBeHandledByClient() {
        return true;
    }

    @Override
    public boolean test(MainAttackTrigger mainAttackTrigger) {
        return true;
    }
}
