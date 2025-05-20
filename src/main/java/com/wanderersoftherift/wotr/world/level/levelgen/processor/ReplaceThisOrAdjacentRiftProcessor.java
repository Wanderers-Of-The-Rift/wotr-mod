package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public interface ReplaceThisOrAdjacentRiftProcessor<T> {

    // the blocks are mostly used just for checking if their faces are full so it might be better te pass results of
    // isFaceFull instead of actual blocks
    int replace(T data, BlockState[] asArray, boolean isHidden);

    T createData(BlockPos structurePos, Vec3i pieceSize);

    static void preloadLayer(
            RiftProcessedRoom room,
            int xOffset,
            int yOffset,
            int zOffset,
            Vec3i pieceSize,
            BlockState[][] preloading,
            boolean[][] booleans) {
        for (int z = 0; z < pieceSize.getZ(); z++) {
            var z2 = z + zOffset;
            var bo = booleans[z + 1];
            var pre = preloading[z + 1];
            for (int x = 0; x < pieceSize.getX(); x++) {
                var state = room.getBlock(x + xOffset, yOffset, z2);
                pre[x + 1] = state;
                bo[x + 1] = false;
            }
        }
    }

    static void saveLayer(
            RiftProcessedRoom room,
            int xOffset,
            int yOffset,
            int zOffset,
            Vec3i pieceSize,
            BlockState[][] preloading,
            boolean[][] booleans) {
        for (int z = 0; z < pieceSize.getZ(); z++) {
            var z2 = z + zOffset;
            var bo = booleans[z + 1];
            var pre = preloading[z + 1];
            for (int x = 0; x < pieceSize.getX(); x++) {
                if (bo[x + 1]) {
                    var state = pre[x + 1];
                    room.setBlock(x + xOffset, yOffset, z2, state);
                }
            }
        }
    }

    public static record ProcessorDataPair<T>(ReplaceThisOrAdjacentRiftProcessor<T> processor, T data) {
        public static <T> ProcessorDataPair<T> create(
                ReplaceThisOrAdjacentRiftProcessor<T> processor,
                BlockPos structurePos,
                Vec3i pieceSize) {
            return new ProcessorDataPair<>(processor, processor.createData(structurePos, pieceSize));
        }

        public int run(BlockState[] asArray, boolean isHidden) {
            return processor.replace(data, asArray, isHidden);
        }
    }
}
