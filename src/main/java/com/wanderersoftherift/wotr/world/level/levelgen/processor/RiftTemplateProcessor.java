package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.util.Ref;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/*
* well... i was told nut to, but i really could't not-do this
* */
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
}
