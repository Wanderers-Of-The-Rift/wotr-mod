package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record RandomRangeRiftParameter(RiftParameterDefinition min, RiftParameterDefinition max)
        implements RegisteredRiftParameter {
    public static final MapCodec<RandomRangeRiftParameter> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    RiftParameterDefinition.CODEC.optionalFieldOf("min", TierRiftParameter.INSTANCE)
                            .forGetter(RandomRangeRiftParameter::min),
                    RiftParameterDefinition.CODEC.optionalFieldOf("max", TierRiftParameter.INSTANCE)
                            .forGetter(RandomRangeRiftParameter::max))
                    .apply(instance, RandomRangeRiftParameter::new));

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        var min = min().getValue(tier, rng, parameterGetter);
        var max = max().getValue(tier, rng, parameterGetter);
        return rng.nextDouble() * (max - min) + min;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
