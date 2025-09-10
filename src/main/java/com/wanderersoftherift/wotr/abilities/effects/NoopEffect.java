package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;

public final class NoopEffect implements AbilityEffect {

    public static final NoopEffect INSTANCE = new NoopEffect();
    public static final MapCodec<NoopEffect> CODEC = MapCodec.unit(INSTANCE);

    private NoopEffect() {
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {

    }
}
