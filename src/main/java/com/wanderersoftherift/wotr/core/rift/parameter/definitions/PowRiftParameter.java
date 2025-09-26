package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record PowRiftParameter(RiftParameter base, RiftParameter exp) implements RegisteredRiftParameter {
    public static final MapCodec<PowRiftParameter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RiftParameter.CODEC.optionalFieldOf("base", TierRiftParameter.INSTANCE).forGetter(PowRiftParameter::base),
            RiftParameter.CODEC.optionalFieldOf("exponent", TierRiftParameter.INSTANCE)
                    .forGetter(PowRiftParameter::exp))
            .apply(instance, PowRiftParameter::new));

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceLocation, Double> parameterGetter) {
        return Math.pow(base().getValue(tier, rng, parameterGetter), exp().getValue(tier, rng, parameterGetter));
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
