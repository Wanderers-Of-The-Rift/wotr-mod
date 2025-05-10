package com.wanderersoftherift.wotr.world.level.levelgen.processor;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/*
* well... i was told nut to, but i really could't not-do this
* */
public interface RiftTemplateProcessor {
    BlockState processBlockState(BlockState currentState, int x, int y, int z, ServerLevel world, BlockPos structurePos, CompoundTag nbt, boolean isVisible);
}
