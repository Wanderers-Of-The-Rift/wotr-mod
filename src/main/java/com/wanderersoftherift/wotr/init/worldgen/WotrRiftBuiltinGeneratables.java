package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PerimeterGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.SingleBlockChunkGeneratable;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRiftBuiltinGeneratables {
    public static final DeferredRegister<MapCodec<? extends RiftGeneratable>> RIFT_BUILTIN_GENERATABLES = DeferredRegister
            .create(WotrRegistries.RIFT_BUILTIN_GENERATABLE_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<PerimeterGeneratable>> CACHED_RIFT_ROOM_GENERATOR = RIFT_BUILTIN_GENERATABLES
            .register("perimeter", () -> PerimeterGeneratable.CODEC);

    public static final Supplier<MapCodec<SingleBlockChunkGeneratable>> LAYER_GENERATABLE_RIFT_ROOM_GENERATOR = RIFT_BUILTIN_GENERATABLES
            .register("single_block_chunk", () -> SingleBlockChunkGeneratable.CODEC);

}
