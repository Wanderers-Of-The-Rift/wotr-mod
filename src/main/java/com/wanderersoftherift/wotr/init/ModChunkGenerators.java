package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.world.level.PocRiftChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>>
        CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<PocRiftChunkGenerator>>
        POC_RIFT_GENERATOR = CHUNK_GENERATORS.register("poc_rift_generator", () -> PocRiftChunkGenerator.CODEC);


}
