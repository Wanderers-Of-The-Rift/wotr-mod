package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PerimeterGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SerializableRiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SingleBlockChunkGeneratable;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrRiftBuiltinGeneratables {
    public static final DeferredRegister<MapCodec<? extends SerializableRiftGeneratable>> RIFT_BUILTIN_GENERATABLES = DeferredRegister
            .create(WotrRegistries.RIFT_BUILTIN_GENERATABLE_TYPES, WanderersOfTheRift.MODID);

    public static final DeferredHolder<MapCodec<? extends SerializableRiftGeneratable>, MapCodec<PerimeterGeneratable>> PERIMETER_GENERATABLE = RIFT_BUILTIN_GENERATABLES
            .register("perimeter", () -> PerimeterGeneratable.CODEC);

    public static final DeferredHolder<MapCodec<? extends SerializableRiftGeneratable>, MapCodec<SingleBlockChunkGeneratable>> SINGLE_BLOCK_CHUNK_GENERATABLE = RIFT_BUILTIN_GENERATABLES
            .register("single_block_chunk", () -> SingleBlockChunkGeneratable.CODEC);

}
