package com.wanderersoftherift.wotr.core.rift.parameter.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;

import java.util.function.Function;

public interface RegisteredRiftParameter extends RiftParameterDefinition {
    Codec<RegisteredRiftParameter> CODEC = WotrRegistries.RIFT_PARAMETER_TYPES.byNameCodec()
            .dispatch(RegisteredRiftParameter::getCodec, Function.identity());

    MapCodec<? extends RegisteredRiftParameter> getCodec();
}
