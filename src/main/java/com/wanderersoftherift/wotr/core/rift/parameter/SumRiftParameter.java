package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.MapCodec;

import java.util.List;

public record SumRiftParameter(List<RiftParameter> values) implements RegisteredRiftParameter {
    public static final MapCodec<SumRiftParameter> CODEC = RiftParameter.CODEC.listOf()
            .xmap(SumRiftParameter::new, SumRiftParameter::values)
            .fieldOf("values");

    public double getValue(int tier) {
        var result = 0.0;
        for (var order : values) {
            result += order.getValue(tier);
        }
        return result;
    }

    @Override
    public MapCodec<? extends RegisteredRiftParameter> getCodec() {
        return CODEC;
    }
}
