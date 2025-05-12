package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public interface ReplaceAirBySurroundingRiftProcessor<T> {

    // the blocks are mostly used just for checking if their faces are full so it might be better te pass results of
    // isFaceFull instead of actual blocks
    BlockState replace(
            T data,
            BlockState up,
            BlockState down,
            BlockState north,
            BlockState south,
            BlockState east,
            BlockState west,
            BlockState[] asArray);

    T createData(BlockPos structurePos, Vec3i pieceSize);

    public static record ProcessorDataPair<T>(ReplaceAirBySurroundingRiftProcessor<T> processor, T data) {
        public static <T> ProcessorDataPair<T> create(
                ReplaceAirBySurroundingRiftProcessor<T> processor,
                BlockPos structurePos,
                Vec3i pieceSize) {
            return new ProcessorDataPair<>(processor, processor.createData(structurePos, pieceSize));
        }

        public BlockState run(
                BlockState up,
                BlockState down,
                BlockState north,
                BlockState south,
                BlockState east,
                BlockState west,
                BlockState[] asArray) {
            return processor.replace(data, up, down, north, south, east, west, asArray);
        }
    }
}
