package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.util.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public interface RiftTemplateProcessor {
    BlockState processBlockState(
            BlockState currentState,
            int x,
            int y,
            int z,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Ref<BlockEntity> entityRef,
            boolean isVisible);

    default StructureTemplate.StructureBlockInfo backportProcess(
            LevelReader world,
            BlockPos piecePos,
            BlockPos structurePos,
            StructureTemplate.StructureBlockInfo rawBlockInfo,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructurePlaceSettings settings,
            @Nullable StructureTemplate template) {
        var entity = new Ref<>((blockInfo.nbt() == null) ? null
                : BlockEntity.loadStatic(blockInfo.pos(), blockInfo.state(), blockInfo.nbt(), world.registryAccess()));
        var newState = processBlockState(blockInfo.state(), blockInfo.pos().getX(), blockInfo.pos().getY(),
                blockInfo.pos().getZ(), (ServerLevelAccessor) world, structurePos, entity, true);
        return new StructureTemplate.StructureBlockInfo(blockInfo.pos(), newState,
                (entity.getValue() != null) ? entity.getValue().saveWithId(world.registryAccess()) : null);
    }
}
