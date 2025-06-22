package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RiftAdjacencyProcessorEvaluator {

    private static void layerReadWriteAcceleration(
            RiftProcessedRoom room,
            int xOffset,
            int yOffset,
            int zOffset,
            Vec3i pieceSize,
            BlockState[][] preloading,
            long[] saveFlags,
            long[] mergedFlags,
            boolean save) {
        var yOffsetMasked = yOffset & 0xf;
        for (int chunkZ = 0; chunkZ <= ((pieceSize.getZ() - 1) >> 4); chunkZ++) {
            var zWithinOutputOffset = chunkZ << 4;
            var zWithinChunkOffset = 0;
            if (chunkZ == 0) {
                zWithinChunkOffset += zOffset & 0xf;
            } else {
                zWithinOutputOffset -= zOffset & 0xf;
            }
            for (int chunkX = 0; chunkX <= ((pieceSize.getX() - 1) >> 4); chunkX++) {
                var xWithinOutputOffset = chunkX << 4;
                var xWithinChunkOffset = 0;
                if (chunkX == 0) {
                    xWithinChunkOffset += xOffset & 0xf;
                } else {
                    xWithinOutputOffset -= xOffset & 0xf;
                }
                if (save) {
                    saveLayerToChunk(room.getChunk(chunkX + (xOffset >> 4), yOffset >> 4, chunkZ + (zOffset >> 4)),
                            yOffsetMasked, xWithinOutputOffset, zWithinOutputOffset, xWithinChunkOffset,
                            zWithinChunkOffset, pieceSize, preloading, saveFlags);
                } else {
                    preloadLayerFromChunk(room.getChunk(chunkX + (xOffset >> 4), yOffset >> 4, chunkZ + (zOffset >> 4)),
                            yOffsetMasked, xWithinOutputOffset, zWithinOutputOffset, xWithinChunkOffset,
                            zWithinChunkOffset, pieceSize, preloading, saveFlags, mergedFlags);
                }
            }
        }
    }

    private static void preloadLayerFromChunk(
            RiftProcessedChunk chunk,
            int yOffsetMasked,
            int xWithinOutputOffset,
            int zWithinOutputOffset,
            int xWithinChunkOffset,
            int zWithinChunkOffset,
            Vec3i pieceSize,
            BlockState[][] preloading,
            long[] saveFlags,
            long[] mergedFlags) {
        int yOffsetMaskedShift = yOffsetMasked << 4;
        if (chunk == null) {
            return;
        }
        for (int z = 0; true; z++) {
            var zWithinOutput = z + zWithinOutputOffset;
            var zWithinChunk = z + zWithinChunkOffset;
            if ((zWithinChunk >= 16) || (zWithinOutput >= pieceSize.getZ())) {
                break;
            }
            saveFlags[zWithinOutput + 1] = 0;
            var preloadingStripe = preloading[zWithinOutput + 1];
            var mergedFlagsStripe = mergedFlags[zWithinOutput + 1];
            for (int x = 0; true; x++) {
                var xWithinOutput = x + xWithinOutputOffset;
                var xWithinChunk = x + xWithinChunkOffset;
                if ((xWithinChunk >= 16) || (xWithinOutput >= pieceSize.getX())) {
                    break;
                }
                preloadingStripe[xWithinOutput + 1] = chunk.getBlockStatePure(xWithinChunk, yOffsetMasked,
                        zWithinChunk);
                var flag = (((chunk.hidden[yOffsetMaskedShift | zWithinChunk] >> xWithinChunk) & 1) != 0)
                        || (((chunk.newlyAdded[yOffsetMaskedShift | zWithinChunk] >> xWithinChunk) & 1) == 0);
                var bits = 1 << (xWithinOutput + 1);
                mergedFlagsStripe &= ~bits;
                if (flag) {
                    mergedFlagsStripe |= bits;
                }
            }
            mergedFlags[zWithinOutput + 1] = mergedFlagsStripe;
        }
    }

    private static void saveLayerToChunk(
            RiftProcessedChunk chunk,
            int yOffsetMasked,
            int xWithinOutputOffset,
            int zWithinOutputOffset,
            int xWithinChunkOffset,
            int zWithinChunkOffset,
            Vec3i pieceSize,
            BlockState[][] preloading,
            long[] saveFlags) {
        if (chunk == null) {
            return;
        }
        for (int z = 0; true; z++) {
            var zWithinOutput = z + zWithinOutputOffset;
            var zWithinChunk = z + zWithinChunkOffset;
            if ((zWithinChunk >= 16) || (zWithinOutput >= pieceSize.getZ())) {
                break;
            }
            var sav = saveFlags[zWithinOutput + 1];
            if (sav == 0) {
                continue;
            }
            var pre = preloading[zWithinOutput + 1];

            for (int x = 0; true; x++) {
                var xWithinOutput = x + xWithinOutputOffset;
                var xWithinChunk = x + xWithinChunkOffset;
                if ((xWithinChunk >= 16) || (xWithinOutput >= pieceSize.getX())) {
                    break;
                }

                var remainingFlags = (sav >> (xWithinOutput + 1));
                if (remainingFlags == 0) {
                    break;
                }
                if ((remainingFlags & 1) != 0) {
                    chunk.setBlockStatePure(xWithinChunk, yOffsetMasked, zWithinChunk, pre[xWithinOutput + 1]);
                }
            }
        }
    }

    public static void applyAdjacencyProcessors(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize,
            List<RiftAdjacencyProcessor<?>> processors) {

        var pairs = new RiftAdjacencyProcessor.ProcessorDataPair<?>[processors.size()];

        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = RiftAdjacencyProcessor.ProcessorDataPair.create(processors.get(i), structurePos, pieceSize,
                    world);
        }

        var directionBlocksArray = new BlockState[7];

        var arrays = new AdjacencyArrays(pieceSize);
        layerReadWriteAcceleration(room, structurePos.getX(), structurePos.getY(), structurePos.getZ(), pieceSize,
                arrays.preloaded[0], arrays.saveMask[0], arrays.mergedFlags[0], false);
        for (int y = 0; y < pieceSize.getY(); y++) {
            layerReadWriteAcceleration(room, structurePos.getX(), structurePos.getY() + y + 1, structurePos.getZ(),
                    pieceSize, arrays.preloaded[(y + 1) & 3], arrays.saveMask[(y + 1) & 3],
                    arrays.mergedFlags[(y + 1) & 1], false);
            arrays.secondaryArrays.load(y);
            for (int z = 0; z < pieceSize.getZ(); z++) {
                arrays.tertiaryArrays.load(y, z);
                for (int x = 0; x < pieceSize.getX(); x++) {
                    if (!arrays.tertiaryArrays.testCenter(x)) {
                        continue;
                    }

                    arrays.tertiaryArrays.getCrossArray(directionBlocksArray, x);
                    if (directionBlocksArray[6] == null) {
                        continue;
                    }
                    int modifyMask = 0;
                    for (int i = 0; i < pairs.length; i++) {
                        modifyMask |= pairs[i].run(directionBlocksArray, false);
                    }
                    arrays.tertiaryArrays.saveCrossArray(directionBlocksArray, x, modifyMask);
                }
                arrays.tertiaryArrays.commitSaveMasks(y, z);
            }
            layerReadWriteAcceleration(room, structurePos.getX(), structurePos.getY() + y - 1, structurePos.getZ(),
                    pieceSize, arrays.preloaded[(y - 1) & 3], arrays.saveMask[(y - 1) & 3], null, true);

        }

        layerReadWriteAcceleration(room, structurePos.getX(), structurePos.getY() + pieceSize.getY() - 1,
                structurePos.getZ(), pieceSize, arrays.preloaded[(pieceSize.getY() - 1) & 3],
                arrays.saveMask[(pieceSize.getY() - 1) & 3], null, true);
    }

    static class AdjacencyArrays {
        public final SecondaryArrays secondaryArrays = new SecondaryArrays();
        public final SecondaryArrays.TertiaryArrays tertiaryArrays = secondaryArrays.tertiaryArrays;
        private final BlockState[][][] preloaded;
        private final long[][] saveMask;
        private final long[][] mergedFlags;

        AdjacencyArrays(Vec3i pieceSize) {
            preloaded = new BlockState[4][pieceSize.getZ() + 2][pieceSize.getX() + 2];
            saveMask = new long[4][pieceSize.getZ() + 2];
            mergedFlags = new long[2][pieceSize.getZ() + 2];
        }

        public class SecondaryArrays {
            public final TertiaryArrays tertiaryArrays = new TertiaryArrays();
            private BlockState[][] preloadedSecondary;
            private long[] saveMaskSecondary;
            private long[] mergedFlagsSecondary;

            public void load(int y) {
                preloadedSecondary = preloaded[y & 3];
                saveMaskSecondary = saveMask[y & 3];
                mergedFlagsSecondary = mergedFlags[y & 1];
            }

            public class TertiaryArrays {

                private long mergedFlagsTertiary;
                private BlockState[] preloadedTertiaryDown;
                private BlockState[] preloadedTertiaryUp;
                private BlockState[] preloadedTertiaryNorth;
                private BlockState[] preloadedTertiarySouth;
                private BlockState[] preloadedTertiaryCenter;
                private long saveMaskTertiaryDown;
                private long saveMaskTertiaryUp;
                private long saveMaskTertiaryNorth;
                private long saveMaskTertiarySouth;
                private long saveMaskTertiaryCenter;

                public void load(int y, int z) {
                    mergedFlagsTertiary = mergedFlagsSecondary[z + 1];
                    preloadedTertiaryDown = preloaded[(y - 1) & 3][z + 1];
                    preloadedTertiaryUp = preloaded[(y + 1) & 3][z + 1];
                    preloadedTertiaryNorth = preloadedSecondary[z];
                    preloadedTertiarySouth = preloadedSecondary[z + 2];
                    preloadedTertiaryCenter = preloadedSecondary[z + 1];
                    saveMaskTertiaryDown = saveMask[(y - 1) & 3][z + 1];
                    saveMaskTertiaryUp = saveMask[(y + 1) & 3][z + 1];
                    saveMaskTertiaryNorth = saveMaskSecondary[z];
                    saveMaskTertiarySouth = saveMaskSecondary[z + 2];
                    saveMaskTertiaryCenter = saveMaskSecondary[z + 1];
                }

                public boolean testCenter(int x) {
                    return ((mergedFlagsTertiary >> (x + 1)) & 1) == 0;
                }

                public void getCrossArray(BlockState[] directionBlocksArray, int x) {
                    directionBlocksArray[0] = preloadedTertiaryDown[x + 1];
                    directionBlocksArray[1] = preloadedTertiaryUp[x + 1];
                    directionBlocksArray[2] = preloadedTertiaryNorth[x + 1];
                    directionBlocksArray[3] = preloadedTertiarySouth[x + 1];
                    directionBlocksArray[4] = preloadedTertiaryCenter[x];
                    directionBlocksArray[5] = preloadedTertiaryCenter[x + 2];
                    directionBlocksArray[6] = preloadedTertiaryCenter[x + 1];
                }

                public void saveCrossArray(BlockState[] directionBlocksArray, int x, int modifyMask) {

                    if (modifyMask != 0) {
                        if ((modifyMask & 0b111) != 0) {
                            if ((modifyMask & 0b1) != 0) {
                                preloadedTertiaryDown[x + 1] = directionBlocksArray[0];
                                saveMaskTertiaryDown |= 1L << (x + 1);
                            }
                            if ((modifyMask & 0b10) != 0) {
                                preloadedTertiaryUp[x + 1] = directionBlocksArray[1];
                                saveMaskTertiaryUp |= 1L << (x + 1);
                            }
                            if ((modifyMask & 0b100) != 0) {
                                preloadedTertiaryNorth[x + 1] = directionBlocksArray[2];
                                saveMaskTertiaryNorth |= 1L << (x + 1);
                            }
                        }
                        if ((modifyMask & 0b111000) != 0) {
                            if ((modifyMask & 0b1000) != 0) {
                                preloadedTertiarySouth[x + 1] = directionBlocksArray[3];
                                saveMaskTertiarySouth |= 1L << (x + 1);
                            }
                            if ((modifyMask & 0b10000) != 0) {
                                preloadedTertiaryCenter[x] = directionBlocksArray[4];
                                saveMaskTertiaryCenter |= 1L << x;
                            }
                            if ((modifyMask & 0b100000) != 0) {
                                preloadedTertiaryCenter[x + 2] = directionBlocksArray[5];
                                saveMaskTertiaryCenter |= 1L << (x + 2);
                            }
                        }

                        if ((modifyMask & 0b1000000) != 0) {
                            preloadedTertiaryCenter[x + 1] = directionBlocksArray[6];
                            saveMaskTertiaryCenter |= 1L << (x + 1);
                        }
                    }
                }

                public void commitSaveMasks(int y, int z) {
                    saveMask[(y - 1) & 3][z + 1] = saveMaskTertiaryDown;
                    saveMask[(y + 1) & 3][z + 1] = saveMaskTertiaryUp;
                    saveMaskSecondary[z] = saveMaskTertiaryNorth;
                    saveMaskSecondary[z + 2] = saveMaskTertiarySouth;
                    saveMaskSecondary[z + 1] = saveMaskTertiaryCenter;
                }
            }
        }
    }
}
