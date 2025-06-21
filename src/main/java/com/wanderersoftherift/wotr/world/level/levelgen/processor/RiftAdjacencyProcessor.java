package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.util.ProcessorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface RiftAdjacencyProcessor<T> {

    int processAdjacency(T data, BlockState[] asArray, boolean isHidden);

    T createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world);

    static void preloadLayer(
            RiftProcessedRoom room,
            int xOffset,
            int yOffset,
            int zOffset,
            Vec3i pieceSize,
            BlockState[][] preloading,
            long[] saveFlags,
            long[] mergedFlags) {
        var yOffsetMasked = yOffset & 0xf;
        var yOffsetMaskedShift = yOffsetMasked << 4;
        for (int chunkZ = 0; chunkZ <= ((pieceSize.getZ() - 1) >> 4); chunkZ++) {
            var z2Delta = chunkZ << 4;
            var z3Delta = 0;
            if (chunkZ == 0) {
                z3Delta += zOffset & 0xf;
            } else {
                z2Delta -= zOffset & 0xf;
            }
            for (int chunkX = 0; chunkX <= ((pieceSize.getX() - 1) >> 4); chunkX++) {
                var chunk = room.getChunk(chunkX + (xOffset >> 4), yOffset >> 4, chunkZ + (zOffset >> 4));
                var x2Delta = chunkX << 4;
                var x3Delta = 0;
                if (chunkX == 0) {
                    x3Delta += xOffset & 0xf;
                } else {
                    x2Delta -= xOffset & 0xf;
                }
                if (chunk == null) {
                    continue;
                }
                for (int z = 0; true; z++) {
                    var z2 = z + z2Delta;
                    var z3 = z + z3Delta;
                    if ((z3 >= 16) || (z2 >= pieceSize.getZ())) {
                        break;
                    }
                    saveFlags[z2 + 1] = 0;
                    var pre = preloading[z2 + 1];
                    var mer = mergedFlags[z2 + 1];
                    for (int x = 0; true; x++) {
                        var x2 = x + x2Delta;
                        var x3 = x + x3Delta;
                        if ((x3 >= 16) || (x2 >= pieceSize.getX())) {
                            break;
                        }
                        var state = chunk.getBlockStatePure(x3, yOffsetMasked, z3);
                        pre[x2 + 1] = state;
                        var merg = (((chunk.hidden[yOffsetMaskedShift | z3] >> x3) & 1) != 0)
                                || (((chunk.newlyAdded[yOffsetMaskedShift | z3] >> x3) & 1) == 0);
                        var bits = 1 << (x2 + 1);
                        mer &= ~bits;
                        if (merg) {
                            mer |= bits;
                        }
                    }
                    mergedFlags[z2 + 1] = mer;
                }
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
            long[] saveFlags) {
        var yOffsetMasked = yOffset & 0xf;
        for (int chunkZ = 0; chunkZ <= ((pieceSize.getZ() - 1) >> 4); chunkZ++) {
            var z2Delta = chunkZ << 4;
            var z3Delta = 0;
            if (chunkZ == 0) {
                z3Delta += zOffset & 0xf;
            } else {
                z2Delta -= zOffset & 0xf;
            }
            for (int chunkX = 0; chunkX <= ((pieceSize.getX() - 1) >> 4); chunkX++) {
                var chunk = room.getChunk(chunkX + (xOffset >> 4), yOffset >> 4, chunkZ + (zOffset >> 4));
                var x2Delta = chunkX << 4;
                var x3Delta = 0;
                if (chunkX == 0) {
                    x3Delta += xOffset & 0xf;
                } else {
                    x2Delta -= xOffset & 0xf;
                }
                if (chunk == null) {
                    continue;
                }
                for (int z = 0; true; z++) {
                    var z2 = z + z2Delta;
                    var z3 = z + z3Delta;
                    if ((z3 >= 16) || (z2 >= pieceSize.getZ())) {
                        break;
                    }
                    var sav = saveFlags[z2 + 1];
                    if (sav == 0) {
                        continue;
                    }
                    var pre = preloading[z2 + 1];

                    for (int x = 0; true; x++) {
                        var x2 = x + x2Delta;
                        var x3 = x + x3Delta;
                        if ((x3 >= 16) || (x2 >= pieceSize.getX())) {
                            break;
                        }

                        var remainingFlags = (sav >> (x2 + 1));
                        if (remainingFlags == 0) {
                            break;
                        }
                        if ((remainingFlags & 1) != 0) {
                            chunk.setBlockStatePure(x3, yOffsetMasked, z3, pre[x2 + 1]);
                        }
                    }
                }
            }
        }
    }

    // stupid mojank why are they applying mirror and rotation to position before processing but to state after
    // processing????
    static <T> List<StructureTemplate.StructureBlockInfo> backportFinalizeProcessing(
            RiftAdjacencyProcessor<T> thiz,
            ServerLevelAccessor serverLevel,
            BlockPos offset,
            BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructurePlaceSettings settings) {
        if (processedBlockInfos.isEmpty()) {
            return Collections.emptyList();
        }
        var modified = new StructureTemplate.StructureBlockInfo[processedBlockInfos.size()];
        BlockPos firstPos = processedBlockInfos.getFirst().pos();
        BlockPos lastPos = processedBlockInfos.getLast().pos();
        var size = new Vec3i(lastPos.getX() - firstPos.getX(), lastPos.getY() - firstPos.getY(),
                lastPos.getZ() - firstPos.getZ());
        var data = thiz.createData(offset, size, serverLevel);
        for (int i = 0; i < processedBlockInfos.size(); i++) {
            StructureTemplate.StructureBlockInfo blockInfo = getInfo(i, processedBlockInfos, modified);
            if (modified[i] == null) {
                modified[i] = blockInfo;
            }
            var down = ProcessorUtil.getBlockIndex(processedBlockInfos, blockInfo.pos().below());
            var up = ProcessorUtil.getBlockIndex(processedBlockInfos, blockInfo.pos().above());
            var north = ProcessorUtil.getBlockIndex(processedBlockInfos, blockInfo.pos().north());
            var south = ProcessorUtil.getBlockIndex(processedBlockInfos, blockInfo.pos().south());
            var west = ProcessorUtil.getBlockIndex(processedBlockInfos, blockInfo.pos().west());
            var east = ProcessorUtil.getBlockIndex(processedBlockInfos, blockInfo.pos().east());

            var array = new BlockState[] { getState(down, processedBlockInfos, modified, settings),
                    getState(up, processedBlockInfos, modified, settings),
                    getState(north, processedBlockInfos, modified, settings),
                    getState(south, processedBlockInfos, modified, settings),
                    getState(west, processedBlockInfos, modified, settings),
                    getState(east, processedBlockInfos, modified, settings),
                    getState(i, processedBlockInfos, modified, settings) };
            var altered = thiz.processAdjacency(data, array, false);
            if ((altered & 0b1) != 0 && down > 0) {
                modified[down] = updateInfo(array[0], getInfo(down, processedBlockInfos, modified), settings);
            }
            if ((altered & 0b10) != 0 && up > 0) {
                modified[up] = updateInfo(array[1], getInfo(up, processedBlockInfos, modified), settings);
            }
            if ((altered & 0b100) != 0 && north > 0) {
                modified[north] = updateInfo(array[2], getInfo(north, processedBlockInfos, modified), settings);
            }
            if ((altered & 0b1000) != 0 && south > 0) {
                modified[south] = updateInfo(array[3], getInfo(south, processedBlockInfos, modified), settings);
            }
            if ((altered & 0b10000) != 0 && west > 0) {
                modified[west] = updateInfo(array[4], getInfo(west, processedBlockInfos, modified), settings);
            }
            if ((altered & 0b100000) != 0 && east > 0) {
                modified[east] = updateInfo(array[5], getInfo(east, processedBlockInfos, modified), settings);
            }
            if ((altered & 0b1000000) != 0) {
                modified[i] = updateInfo(array[6], blockInfo, settings);
            }
        }
        return Arrays.stream(modified).filter(Objects::nonNull).toList();
    }

    private static StructureTemplate.StructureBlockInfo updateInfo(
            BlockState newState,
            StructureTemplate.StructureBlockInfo oldInfo,
            StructurePlaceSettings settings) {
        if (oldInfo == null) {
            return null;
        }
        return new StructureTemplate.StructureBlockInfo(oldInfo.pos(),
                newState.rotate(oppositeRotation(settings.getRotation())).mirror(settings.getMirror()), oldInfo.nbt());
    }

    private static Rotation oppositeRotation(Rotation rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> rotation;
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
        };
    }

    private static BlockState getState(
            int i,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructureTemplate.StructureBlockInfo[] modified,
            StructurePlaceSettings settings) {
        var info = getInfo(i, processedBlockInfos, modified);
        if (info == null) {
            return null;
        }
        return info.state().mirror(settings.getMirror()).rotate(settings.getRotation());
    }

    private static StructureTemplate.StructureBlockInfo getInfo(
            int i,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructureTemplate.StructureBlockInfo[] modified) {
        if (i < 0) {
            return null;
        }
        var mod = modified[i];
        if (mod != null) {
            return mod;
        }
        return processedBlockInfos.get(i);

    }

    public static record ProcessorDataPair<T>(RiftAdjacencyProcessor<T> processor, T data) {
        public static <T> ProcessorDataPair<T> create(
                RiftAdjacencyProcessor<T> processor,
                BlockPos structurePos,
                Vec3i pieceSize,
                ServerLevelAccessor world) {
            return new ProcessorDataPair<>(processor, processor.createData(structurePos, pieceSize, world));
        }

        public int run(BlockState[] asArray, boolean isHidden) {
            return processor.processAdjacency(data, asArray, isHidden);
        }
    }
}
