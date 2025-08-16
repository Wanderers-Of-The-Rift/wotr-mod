package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;

import java.util.function.Function;

public interface SerializableCorridorValidator extends CorridorValidator {

    Codec<SerializableCorridorValidator> CODEC = WotrRegistries.RIFT_CORRIDOR_VALIDATORS.byNameCodec()
            .dispatch(SerializableCorridorValidator::codec, Function.identity());

    MapCodec<? extends SerializableCorridorValidator> codec();

}
