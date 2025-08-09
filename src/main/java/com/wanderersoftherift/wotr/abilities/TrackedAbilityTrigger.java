package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.MapCodec;

public interface TrackedAbilityTrigger {

    MapCodec<? extends TrackedAbilityTrigger> codec();
}
