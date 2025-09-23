package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

public interface RiftParameter {
    Codec<RiftParameter> CODEC = Codec.either(ConstantRiftParameter.CODEC, RegisteredRiftParameter.CODEC)
            .xmap(it -> it.map(it2 -> it2, it2 -> it2), it -> switch (it) {
                case ConstantRiftParameter constantRiftParameterType -> Either.left(constantRiftParameterType);
                case RegisteredRiftParameter registeredRiftParameterType -> Either.right(registeredRiftParameterType);
                default -> throw new IllegalStateException("Unexpected value: " + it);
            });

    double getValue(int tier);
}
