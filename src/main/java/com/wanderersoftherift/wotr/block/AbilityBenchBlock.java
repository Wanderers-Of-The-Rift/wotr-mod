package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.AbilityBenchBlockEntity;
import com.wanderersoftherift.wotr.util.EnumEntries;
import com.wanderersoftherift.wotr.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Block for the workbench allowing ability assignment and upgrade
 */
public class AbilityBenchBlock extends BaseEntityBlock {
    public static final MapCodec<AbilityBenchBlock> CODEC = simpleCodec(AbilityBenchBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final Component CONTAINER_TITLE = Component
            .translatable("container." + WanderersOfTheRift.MODID + ".ability_bench");
    // spotless:off
    private static final VoxelShape SHAPE = VoxelShapeUtils.combine(
            Block.box(4.0, 0.75, 5.0, 11.0, 1.75, 10.0),
            Block.box(3.0, 1.75, 4.0, 11.0, 5.0, 12.0),
            Block.box(5.0, 1.75, 5.0, 15.0, 5.5, 11.0),
            Block.box(0.0, 4.75, 1.0, 12.0, 9, 15.0),
            Block.box(12.0, 5.28, 3.2, 16.0, 11, 13),
            Block.box(2.0, 11.5, 7.0, 10.0, 17.5, 10.0)
            );
    // spotless:on
    private static final Map<Direction, VoxelShape> SHAPES;

    static {
        var builder = ImmutableMap.<Direction, VoxelShape>builder();
        for (Direction dir : EnumEntries.DIRECTIONS_HORIZONTAL) {
            builder.put(dir, VoxelShapeUtils.rotateHorizontal(SHAPE, dir));
        }
        SHAPES = builder.build();
    }

    public AbilityBenchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return SHAPES.get(state.getValue(HorizontalDirectionalBlock.FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    protected MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> {
            if (level.getBlockEntity(pos) instanceof AbilityBenchBlockEntity blockEntity) {
                return blockEntity.createMenu(containerId, playerInventory, player);
            }
            return null;
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
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AbilityBenchBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull BlockState newState,
            boolean isMoving) {
        if (!newState.is(state.getBlock()) && level.getBlockEntity(pos) instanceof AbilityBenchBlockEntity entity) {
            entity.dropContents();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
