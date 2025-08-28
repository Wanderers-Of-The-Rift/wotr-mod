package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

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
        var yOffsetMasked = yOffset & RiftProcessedChunk.CHUNK_HEIGHT_MASK;
        for (int chunkZ = 0; chunkZ <= ((pieceSize.getZ() - 1) >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT); chunkZ++) {
            var zWithinOutputOffset = chunkZ << RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
            var zWithinChunkOffset = 0;
            if (chunkZ == 0) {
                zWithinChunkOffset += zOffset & RiftProcessedChunk.CHUNK_WIDTH_MASK;
            } else {
                zWithinOutputOffset -= zOffset & RiftProcessedChunk.CHUNK_WIDTH_MASK;
            }
            for (int chunkX = 0; chunkX <= ((pieceSize.getX() - 1) >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT); chunkX++) {
                var xWithinOutputOffset = chunkX << RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
                var xWithinChunkOffset = 0;
                if (chunkX == 0) {
                    xWithinChunkOffset += xOffset & RiftProcessedChunk.CHUNK_WIDTH_MASK;
                } else {
                    xWithinOutputOffset -= xOffset & RiftProcessedChunk.CHUNK_WIDTH_MASK;
                }
                if (save) {
                    saveLayerToChunk(
                            room.getChunk(chunkX + (xOffset >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT),
                                    yOffset >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT,
                                    chunkZ + (zOffset >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT)),
                            yOffsetMasked, xWithinOutputOffset, zWithinOutputOffset, xWithinChunkOffset,
                            zWithinChunkOffset, pieceSize, preloading, saveFlags);
                } else {
                    preloadLayerFromChunk(
                            room.getChunk(chunkX + (xOffset >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT),
                                    yOffset >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT,
                                    chunkZ + (zOffset >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT)),
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
            if ((zWithinChunk >= LevelChunkSection.SECTION_WIDTH) || (zWithinOutput >= pieceSize.getZ())) {
                break;
            }
            saveFlags[zWithinOutput + 1] = 0;
            var preloadingStripe = preloading[zWithinOutput + 1];
            var mergedFlagsStripe = mergedFlags[zWithinOutput + 1];
            for (int x = 0; true; x++) {
                var xWithinOutput = x + xWithinOutputOffset;
                var xWithinChunk = x + xWithinChunkOffset;
                if ((xWithinChunk >= LevelChunkSection.SECTION_WIDTH) || (xWithinOutput >= pieceSize.getX())) {
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
            if ((zWithinChunk >= LevelChunkSection.SECTION_WIDTH) || (zWithinOutput >= pieceSize.getZ())) {
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
                if ((xWithinChunk >= LevelChunkSection.SECTION_WIDTH) || (xWithinOutput >= pieceSize.getX())) {
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

}
