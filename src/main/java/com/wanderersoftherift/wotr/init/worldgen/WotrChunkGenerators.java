package com.wanderersoftherift.wotr.init.worldgen;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import com.wanderersoftherift.wotr.world.level.SingleBlockGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister
            .create(Registries.CHUNK_GENERATOR, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<SingleBlockGenerator>> SINGLE_BLOCK_GENERATOR = CHUNK_GENERATORS
            .register("single_block_generator", () -> SingleBlockGenerator.CODEC);
    public static final Supplier<MapCodec<FastRiftGenerator>> FAST_RIFT_GENERATOR = CHUNK_GENERATORS
            .register("fast_rift_generator", () -> FastRiftGenerator.CODEC);

}
