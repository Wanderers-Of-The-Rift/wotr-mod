package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.block.blockentity.RiftChestBlockEntity;
import com.wanderersoftherift.wotr.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RiftChestEntityBlock extends ChestBlock {
    public static final MapCodec<RiftChestEntityBlock> CODEC = simpleCodec((properties) -> new RiftChestEntityBlock(ModBlockEntities.RIFT_CHEST::get, properties));
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    private static final Component CONTAINER_TITLE = Component.translatable("container.wotr.rift_chest");

    public RiftChestEntityBlock(Supplier<BlockEntityType<? extends ChestBlockEntity>> riftChest, Properties properties) {
        super(riftChest, properties);
    }

    @Override
    public @NotNull MapCodec<RiftChestEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RiftChestBlockEntity(pos, state);
    }
}
