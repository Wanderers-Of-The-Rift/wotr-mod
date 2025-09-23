package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.MapCodec;

public class TierRiftParameter implements RegisteredRiftParameter {
    public static final TierRiftParameter INSTANCE = new TierRiftParameter();
    public static final MapCodec<TierRiftParameter> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public double getValue(int tier) {
        return tier;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
