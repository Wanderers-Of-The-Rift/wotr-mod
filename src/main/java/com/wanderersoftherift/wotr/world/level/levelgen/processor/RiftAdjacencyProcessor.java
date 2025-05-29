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

    // the blocks are mostly used just for checking if their faces are full so it might be better te pass results of
    // isFaceFull instead of actual blocks
    int processAdjacency(T data, BlockState[] asArray, boolean isHidden);

    T createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world);

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

    static void preloadMerged(
            RiftProcessedRoom room,
            int xOffset,
            int yOffset,
            int zOffset,
            Vec3i pieceSize,
            boolean[][] booleans) {
        for (int z = 0; z < pieceSize.getZ(); z++) {
            var z2 = z + zOffset;
            var bo = booleans[z];
            for (int x = 0; x < pieceSize.getX(); x++) {
                bo[x] = room.getMerged(x + xOffset, yOffset, z2);
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
