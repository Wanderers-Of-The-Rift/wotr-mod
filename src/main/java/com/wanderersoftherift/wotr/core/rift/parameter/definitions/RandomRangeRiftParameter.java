package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record RandomRangeRiftParameter(RiftParameter min, RiftParameter max) implements RegisteredRiftParameter {
    public static final MapCodec<RandomRangeRiftParameter> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    RiftParameter.CODEC.optionalFieldOf("min", TierRiftParameter.INSTANCE)
                            .forGetter(RandomRangeRiftParameter::min),
                    RiftParameter.CODEC.optionalFieldOf("max", TierRiftParameter.INSTANCE)
                            .forGetter(RandomRangeRiftParameter::max))
                    .apply(instance, RandomRangeRiftParameter::new));

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceLocation, Double> parameterGetter) {
        var min = min().getValue(tier, rng, parameterGetter);
        var max = max().getValue(tier, rng, parameterGetter);
        return rng.nextDouble() * (max - min) + min;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
