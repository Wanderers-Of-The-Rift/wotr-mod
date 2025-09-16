package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.AnomalyBlock;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.worldgen.WotrProcessors;
import com.wanderersoftherift.wotr.util.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class AnomalyProcessor extends StructureProcessor implements RiftTemplateProcessor {

    public static final MapCodec<AnomalyProcessor> CODEC = MapCodec.unit(new AnomalyProcessor());

    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @javax.annotation.Nullable StructureTemplate template) {
        return backportProcess(world, piecePos, structurePos, rawBlockInfo, blockInfo, settings, template);
    }

    @Override
    public BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Ref<BlockEntity> entityRef,
            boolean isVisible) {
        if (currentState.getBlock() != Blocks.END_ROD) {
            return currentState;
        }
        currentState = WotrBlocks.ANOMALY.get()
                .defaultBlockState()
                .setValue(AnomalyBlock.FACING, currentState.getValue(EndRodBlock.FACING));
        var blockEntity = new AnomalyBlockEntity(new BlockPos(x, y, z), currentState);
        // todo initialize block entity
        entityRef.setValue(blockEntity);
        return currentState;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WotrProcessors.ANOMALY.get();
    }
}
