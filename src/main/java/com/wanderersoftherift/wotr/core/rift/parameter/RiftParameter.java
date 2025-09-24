package com.wanderersoftherift.wotr.core.rift.parameter;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.Holder;

public interface RiftParameter {
    Codec<RiftParameter> CODEC = Codec.either(ConstantRiftParameter.CODEC, RegisteredRiftParameter.CODEC)
            .xmap(it -> it.map(it2 -> it2, it2 -> it2), it -> switch (it) {
                case ConstantRiftParameter constantRiftParameterType -> Either.left(constantRiftParameterType);
                case RegisteredRiftParameter registeredRiftParameterType -> Either.right(registeredRiftParameterType);
                default -> throw new IllegalStateException("Unexpected value: " + it);
            });

    Codec<Holder<RiftParameter>> HOLDER_CODEC = LaxRegistryCodec.ref(WotrRegistries.Keys.RIFT_PARAMETER_CONFIGS);

    double getValue(int tier);
}
