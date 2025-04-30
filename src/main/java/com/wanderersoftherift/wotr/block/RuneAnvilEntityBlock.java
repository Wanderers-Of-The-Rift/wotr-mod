package com.wanderersoftherift.wotr.block;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.RuneAnvilBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuneAnvilEntityBlock extends BaseEntityBlock {
    public static final MapCodec<RuneAnvilEntityBlock> CODEC = simpleCodec(RuneAnvilEntityBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.wotr.rune_anvil");

    public RuneAnvilEntityBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public @NotNull MapCodec<RuneAnvilEntityBlock> codec() {
        return CODEC;
    }

    protected MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> {
            RuneAnvilBlockEntity blockEntity = (RuneAnvilBlockEntity) level.getBlockEntity(pos);
            if (blockEntity == null) {
                return null;
            } else {
                return blockEntity.createMenu(containerId, playerInventory, player);
            }
        }, CONTAINER_TITLE);
    }

    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new RuneAnvilBlockEntity(blockPos, blockState);
    }
}