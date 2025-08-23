package com.wanderersoftherift.wotr.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.world.level.FastRiftGenerator;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.function.Function;

/*
 * Don't use this much since it runs separately from the majority of rift generation
 * only when you need to access block in other neighbouring rooms
 */
public interface RiftPostProcessingStep {

    Codec<RiftPostProcessingStep> CODEC = WotrRegistries.RIFT_POST_STEPS.byNameCodec()
            .dispatch(RiftPostProcessingStep::codec, Function.identity());

    MapCodec<? extends RiftPostProcessingStep> codec();

    void runPostProcessing(
            FastRiftGenerator generator,
            ChunkAccess chunk,
            PositionalRandomFactory randomFactory,
            WorldGenLevel level);
}
