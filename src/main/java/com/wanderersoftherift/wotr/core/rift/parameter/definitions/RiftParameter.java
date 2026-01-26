package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;

import java.util.function.Function;

public record RiftParameter(RiftParameterDefinition defaultValue) implements RiftParameterDefinition {

    public static final Codec<Holder<RiftParameter>> HOLDER_CODEC = LaxRegistryCodec
            .ref(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS);
    public static final Codec<RiftParameter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RiftParameterDefinition.CODEC.fieldOf("initializer").forGetter(RiftParameter::defaultValue)
    ).apply(instance, RiftParameter::new));

    public double getValue(int tier, RandomSource rng, Function<ResourceKey<RiftParameter>, Double> parameterGetter) {
        return defaultValue.getValue(tier, rng, parameterGetter);
    }
}
