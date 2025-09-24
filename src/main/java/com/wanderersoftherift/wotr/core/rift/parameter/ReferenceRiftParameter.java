package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record ReferenceRiftParameter(Holder<RiftParameter> value) implements RiftParameter {
    public static final Codec<ReferenceRiftParameter> CODEC = RiftParameter.HOLDER_CODEC
            .xmap(ReferenceRiftParameter::new, ReferenceRiftParameter::value);

    @Override
    public double getValue(int tier, RandomSource rng, Function<ResourceLocation, Double> parameterGetter) {
        return parameterGetter.apply(value().getKey().location());
    }
}
