package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.RuneAnvilBlockEntity;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RuneAnvilEntityBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<RuneAnvilEntityBlock> CODEC = simpleCodec(RuneAnvilEntityBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.wotr.rune_anvil");
    private static final VoxelShape SHAPE = makeShape();

    private static final Map<Direction, VoxelShape> SHAPES;

    static {
        ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
        builder.put(Direction.NORTH, SHAPE);
        builder.put(Direction.EAST, VoxelShapeUtils.rotateHorizontal(SHAPE, Direction.EAST));
        builder.put(Direction.SOUTH, VoxelShapeUtils.rotateHorizontal(SHAPE, Direction.SOUTH));
        builder.put(Direction.WEST, VoxelShapeUtils.rotateHorizontal(SHAPE, Direction.WEST));
        SHAPES = builder.build();
    }

    public RuneAnvilEntityBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.125, 0.8125, 0.25, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.25, 0.1875, 0.75, 0.3125, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.3125, 0.3125, 0.625, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.625, 0, 0.75, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6625, 0.75, 0.006_25, 0.85, 0.875, 0.256_25), BooleanOp.OR);

        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
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