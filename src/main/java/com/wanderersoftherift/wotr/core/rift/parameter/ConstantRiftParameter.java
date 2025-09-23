package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.Codec;

public record ConstantRiftParameter(double value) implements RiftParameter {
    public static final Codec<ConstantRiftParameter> CODEC = Codec.DOUBLE.xmap(ConstantRiftParameter::new,
            ConstantRiftParameter::value);

    @Override
    public double getValue(int tier) {
        return value;
    }
}
