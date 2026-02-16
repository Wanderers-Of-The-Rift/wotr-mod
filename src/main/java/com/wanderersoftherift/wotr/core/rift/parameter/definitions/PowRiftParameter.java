package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record PowRiftParameter(RiftParameterDefinition base, RiftParameterDefinition exp)
        implements RegisteredRiftParameter {
    public static final MapCodec<PowRiftParameter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RiftParameterDefinition.CODEC.optionalFieldOf("base", TierRiftParameter.INSTANCE)
                    .forGetter(PowRiftParameter::base),
            RiftParameterDefinition.CODEC.optionalFieldOf("exponent", TierRiftParameter.INSTANCE)
                    .forGetter(PowRiftParameter::exp))
            .apply(instance, PowRiftParameter::new));

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        return Math.pow(base().getValue(tier, rng, parameterGetter), exp().getValue(tier, rng, parameterGetter));
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
