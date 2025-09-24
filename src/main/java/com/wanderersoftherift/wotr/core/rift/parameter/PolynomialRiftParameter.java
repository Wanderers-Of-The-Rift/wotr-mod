package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.function.Function;

public record PolynomialRiftParameter(List<RiftParameter> orderParameters, RiftParameter position)
        implements RegisteredRiftParameter {
    public static final MapCodec<PolynomialRiftParameter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(RiftParameter.CODEC.listOf().fieldOf("orders").forGetter(PolynomialRiftParameter::orderParameters),
                    RiftParameter.CODEC.optionalFieldOf("position", TierRiftParameter.INSTANCE)
                            .forGetter(PolynomialRiftParameter::position))
            .apply(instance, PolynomialRiftParameter::new));

    public double getValue(int tier, RandomSource rng, Function<ResourceLocation, Double> parameterGetter) {
        var x = position.getValue(tier, rng, parameterGetter);
        var x2 = 1.0;
        var result = 0.0;
        for (var order : orderParameters) {
            result += order.getValue(tier, rng, parameterGetter) * x2;
            x2 *= x;
        }
        return result;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
