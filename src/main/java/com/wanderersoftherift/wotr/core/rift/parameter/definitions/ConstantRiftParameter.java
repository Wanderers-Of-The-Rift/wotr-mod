package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record ConstantRiftParameter(double value) implements RiftParameter {
    public static final Codec<ConstantRiftParameter> CODEC = Codec.DOUBLE.xmap(ConstantRiftParameter::new,
            ConstantRiftParameter::value);

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        return value;
    }
}
