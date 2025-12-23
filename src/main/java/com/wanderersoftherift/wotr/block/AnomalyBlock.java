package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AnomalyBlock extends BaseEntityBlock {
    public static final MapCodec<AnomalyBlock> CODEC = simpleCodec(AnomalyBlock::new);
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.UP, Block.box(6.0, 0.0, 6.0, 10.0, 1.0, 10.0), Direction.DOWN,
            Block.box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0), Direction.NORTH, Block.box(6.0, 6.0, 15.0, 10.0, 10.0, 16.0),
            Direction.SOUTH, Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 1.0), Direction.WEST,
            Block.box(15.0, 6.0, 6.0, 16.0, 10.0, 10.0), Direction.EAST, Block.box(0.0, 6.0, 6.0, 1.0, 10.0, 10.0)));

    private static final Map<Direction, VoxelShape> INTERACTION_SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.UP, Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0), Direction.DOWN,
            Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0), Direction.NORTH, Block.box(6.0, 2.0, 0.0, 10.0, 14.0, 16.0),
            Direction.SOUTH, Block.box(6.0, 2.0, 0.0, 10.0, 14.0, 16.0), Direction.WEST,
            Block.box(0.0, 2.0, 6.0, 16.0, 14.0, 10.0), Direction.EAST, Block.box(0.0, 2.0, 6.0, 16.0, 14.0, 10.0)));

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public AnomalyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (level.getBlockEntity(pos) instanceof AnomalyBlockEntity anomalyBlockEntity) {
            anomalyBlockEntity.scheduledTick(level, pos, state);
        }
    }

    @Override
    public @NotNull MapCodec<AnomalyBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnomalyBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(
            // Logic handled in the block entity
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof AnomalyBlockEntity anomalyEntity) {
            return anomalyEntity.interact(player, InteractionHand.MAIN_HAND);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return INTERACTION_SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> blockEntityType) {
        if (level.isClientSide && blockEntityType == WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (level1.isClientSide() && blockEntity instanceof AnomalyBlockEntity anomaly) {
                    anomaly.clientTick(pos);
                }
            };
        }
        return null;
    }
}
