package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.util.EnumEntries;
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

    /**
     * Runs the processor on patch of blocks
     *
     * @param data           value computed by {@link #createData}
     * @param adjacentBlocks Array of size 7. First 6 values are blocks adjecent to the central block, last is the
     *                       central block itself
     * @param isHidden       whether central block is fully surrounded by solid blocks
     * @return bitmask denoting which blocks in adjacentBlocks were modified
     */
    int processAdjacency(T data, BlockState[] adjacentBlocks, boolean isHidden);

    /**
     * Creates data used as 1st parameter of {@link #processAdjacency}, called once per template
     */
    T createData(BlockPos structurePos, Vec3i pieceSize, ServerLevelAccessor world);

    // stupid mojank why are they applying mirror and rotation to position before processing but to state after
    // processing????
    static <T> List<StructureTemplate.StructureBlockInfo> backportFinalizeProcessing(
            RiftAdjacencyProcessor<T> processor,
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
        var data = processor.createData(offset, size, serverLevel);

        var stateArray = new BlockState[7];
        var directionalIndices = new int[7];

        for (int i = 0; i < processedBlockInfos.size(); i++) {
            StructureTemplate.StructureBlockInfo blockInfo = getInfo(i, processedBlockInfos, modified);
            if (modified[i] == null) {
                modified[i] = blockInfo;
            }

            loadIndices(directionalIndices, processedBlockInfos, blockInfo.pos());
            directionalIndices[6] = i;

            stateArray[6] = getState(i, processedBlockInfos, modified, settings);
            loadStates(stateArray, directionalIndices, processedBlockInfos, modified, settings);

            var altered = processor.processAdjacency(data, stateArray, false);

            commitStates(stateArray, directionalIndices, processedBlockInfos, modified, settings, altered);
        }
        return Arrays.stream(modified).filter(Objects::nonNull).toList();
    }

    private static void commitStates(
            BlockState[] stateArray,
            int[] directionalIndices,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructureTemplate.StructureBlockInfo[] modified,
            StructurePlaceSettings settings,
            int altered) {
        for (int i = 0; i < directionalIndices.length; i++) {
            var index = directionalIndices[i];
            if ((altered & (1 << i)) != 0 && index > 0) {
                modified[i] = updateInfo(stateArray[0], getInfo(index, processedBlockInfos, modified), settings);
            }
        }
    }

    private static void loadStates(
            BlockState[] stateArray,
            int[] directionalIndices,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            StructureTemplate.StructureBlockInfo[] modified,
            StructurePlaceSettings settings) {
        for (int i = 0; i < directionalIndices.length; i++) {
            stateArray[i] = getState(directionalIndices[i], processedBlockInfos, modified, settings);
        }
    }

    private static void loadIndices(
            int[] directionalIndices,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfos,
            BlockPos pos) {
        for (int i = 0; i < EnumEntries.DIRECTIONS.size(); i++) {
            directionalIndices[i] = ProcessorUtil.getBlockIndex(processedBlockInfos,
                    pos.relative(EnumEntries.DIRECTIONS.get(i)));
        }
    }

    private static StructureTemplate.StructureBlockInfo updateInfo(
            BlockState newState,
            StructureTemplate.StructureBlockInfo oldInfo,
            StructurePlaceSettings settings) {
        if (oldInfo == null || newState == null) {
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

    public record ProcessorDataPair<T>(RiftAdjacencyProcessor<T> processor, T data) {
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
