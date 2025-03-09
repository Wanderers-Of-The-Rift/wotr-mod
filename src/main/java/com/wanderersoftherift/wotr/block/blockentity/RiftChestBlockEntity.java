package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RiftChestBlockEntity extends ChestBlockEntity {

    public RiftChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.RIFT_CHEST.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.wotr.rift_chest");
    }
}