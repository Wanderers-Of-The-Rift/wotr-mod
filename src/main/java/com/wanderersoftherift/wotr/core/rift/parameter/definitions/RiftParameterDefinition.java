package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

/**
 * Interface for elements that compose a rift parameter definition - either in part or in whole
 */
public interface RiftParameterDefinition {
    Codec<RiftParameterDefinition> CODEC = Codec
            .either(Codec.either(ConstantRiftParameter.CODEC, ReferenceRiftParameter.CODEC),
                    RegisteredRiftParameter.CODEC)
            .xmap(it -> it.map(it2 -> it2.map(it3 -> it3, it3 -> it3), it2 -> it2), it -> switch (it) {
                case ConstantRiftParameter constantRiftParameterType ->
                    Either.left(Either.left(constantRiftParameterType));
                case RegisteredRiftParameter registeredRiftParameterType -> Either.right(registeredRiftParameterType);
                case ReferenceRiftParameter referenceRiftParameterType ->
                    Either.left(Either.right(referenceRiftParameterType));
                default -> throw new IllegalStateException("Unexpected value: " + it);
            });

    double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter);
}
