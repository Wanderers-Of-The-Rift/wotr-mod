package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;

import java.util.function.Function;

public interface SerializableRiftGeneratable extends RiftGeneratable {

    Codec<SerializableRiftGeneratable> BUILTIN_GENERATABLE_CODEC = WotrRegistries.RIFT_BUILTIN_GENERATABLE_TYPES
            .byNameCodec()
            .dispatch(SerializableRiftGeneratable::codec, Function.identity());

    MapCodec<? extends SerializableRiftGeneratable> codec();
}
