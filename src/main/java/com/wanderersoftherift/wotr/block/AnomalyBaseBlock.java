package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * An Anomaly Base block is a usable block for generating anomalies.
 */
public class AnomalyBaseBlock extends BaseEntityBlock {
    public static final MapCodec<AnomalyBaseBlock> CODEC = simpleCodec(AnomalyBaseBlock::new);
    // spotless:off
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
            Direction.UP, Block.box(6.0, 0.0, 6.0, 10.0, 1.0, 10.0),
            Direction.DOWN, Block.box(6.0, 15.0, 6.0, 10.0, 16.0, 10.0),
            Direction.NORTH, Block.box(6.0, 6.0, 15.0, 10.0, 10.0, 16.0),
            Direction.SOUTH, Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 1.0),
            Direction.WEST, Block.box(15.0, 6.0, 6.0, 16.0, 10.0, 10.0),
            Direction.EAST, Block.box(0.0, 6.0, 6.0, 1.0, 10.0, 10.0)));
    // spotless:on

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public AnomalyBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnomalyBlockEntity(pos, state); // Create a new block entity for the anomaly
    }

    @Override
    public @NotNull MapCodec<AnomalyBaseBlock> codec() {
        return CODEC;
    }

    @Override
    public InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof AnomalyBlockEntity anomalyEntity) {
            return anomalyEntity.onAnomalyClick(player, InteractionHand.MAIN_HAND);
        }
        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL; // For custom rendering
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(); // Return empty list - no drops
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Make the hitbox match the rendered anomaly size (0.5x0.5x0.5 blocks centered)
        return Block.box(4, 2, 4, 12, 14, 12); // 0.25 to 0.75 in each direction (centered 0.5 size)
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
        if (!level.isClientSide && blockEntityType == WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (level1 instanceof ServerLevel serverLevel) {
                    AnomalyBlockEntity.tick(serverLevel, pos, state1, (AnomalyBlockEntity) blockEntity);
                }
            };
        }
        return null;
    }

//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
//            Level level,
//            BlockState state,
//            BlockEntityType<T> blockEntityType) {
//        if (!level.isClientSide && blockEntityType == WotrBlockEntities.ANOMALY_BLOCK_ENTITY.get()) {
//            return (level1, pos, state1, blockEntity) -> {
//                if (level1 instanceof ServerLevel serverLevel) {
//                    AnomalyBlockEntity.tick(serverLevel, pos, state1, (AnomalyBlockEntity) blockEntity);
//                }
//            };
//        }
//        return null;
//    }
}
