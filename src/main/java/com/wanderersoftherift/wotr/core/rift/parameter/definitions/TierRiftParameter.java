package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public class TierRiftParameter implements RegisteredRiftParameter {
    public static final TierRiftParameter INSTANCE = new TierRiftParameter();
    public static final MapCodec<TierRiftParameter> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceLocation, Double> parameterGetter) {
        return tier;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
