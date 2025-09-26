package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.function.Function;

public record SumRiftParameter(List<RiftParameter> values) implements RegisteredRiftParameter {
    public static final MapCodec<SumRiftParameter> CODEC = RiftParameter.CODEC.listOf()
            .xmap(SumRiftParameter::new, SumRiftParameter::values)
            .fieldOf("values");

    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        var result = 0.0;
        for (var order : values) {
            result += order.getValue(tier, rng, parameterGetter);
        }
        return result;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
