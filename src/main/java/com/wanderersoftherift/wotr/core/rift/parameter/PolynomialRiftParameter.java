package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record PolynomialRiftParameter(List<RiftParameter> orderParameters, RiftParameter position)
        implements RegisteredRiftParameter {
    public static final MapCodec<PolynomialRiftParameter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(RiftParameter.CODEC.listOf().fieldOf("orders").forGetter(PolynomialRiftParameter::orderParameters),
                    RiftParameter.CODEC.optionalFieldOf("position", TierRiftParameter.INSTANCE)
                            .forGetter(PolynomialRiftParameter::position))
            .apply(instance, PolynomialRiftParameter::new));

    public double getValue(int tier) {
        var x = 1.0;
        var result = 0.0;
        for (var order : orderParameters) {
            result += order.getValue(tier) * x;
            x *= tier;
        }
        return result;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
