package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;

public record TickTrigger() implements TrackedAbilityTrigger {
    public static final TickTrigger INSTANCE = new TickTrigger();
    public static final MapCodec<TickTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.point(INSTANCE));

    @Override
    public MapCodec<? extends TrackedAbilityTrigger> codec() {
        return CODEC;
    }

}
