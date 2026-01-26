package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record ReferenceRiftParameter(Holder<RiftParameter> value) implements RiftParameterDefinition {
    public static final Codec<ReferenceRiftParameter> CODEC = RiftParameter.HOLDER_CODEC
            .xmap(ReferenceRiftParameter::new, ReferenceRiftParameter::value);

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        return parameterGetter.apply(value().getKey());
    }
}
