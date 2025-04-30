package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * A Rift Spawner block is a usable block for generating rift entrances.
 */
public class RiftSpawnerBlock extends Block {
    public static final MapCodec<RiftSpawnerBlock> CODEC = simpleCodec(RiftSpawnerBlock::new);
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.UP,
            Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0), Direction.DOWN, Block.box(0.0, 3.0, 0.0, 16.0, 16.0, 16.0),
            Direction.NORTH, Block.box(0.0, 0.0, 3.0, 16.0, 16.0, 16.0), Direction.SOUTH,
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 13.0), Direction.WEST, Block.box(3.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Direction.EAST, Block.box(0.0, 0.0, 0.0, 13.0, 16.0, 16.0)));

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public RiftSpawnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public @NotNull MapCodec<RiftSpawnerBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    /**
     * Provides the location to create the rift, if a valid location exists
     * 
     * @param level
     * @param pos
     * @param dir
     * @return A valid spawn location, or Optional#empty
     */
    public Optional<PortalSpawnLocation> getSpawnLocation(Level level, BlockPos pos, Direction dir) {
        Direction facing = level.getBlockState(pos).getValue(BlockStateProperties.FACING);
        if (facing.getAxis().isVertical()) {
            BlockPos checkPos = pos;
            for (int i = 0; i < 3; i++) {
                checkPos = checkPos.relative(facing);
                if (!allowsPortal(level, checkPos)) {
                    return Optional.empty();
                }
            }
            if (facing == Direction.UP) {
                return Optional.of(new PortalSpawnLocation(pos.above().getBottomCenter(), Direction.UP));
            } else {
                BlockPos origin = pos.relative(dir, 3);
                return Optional.of(new PortalSpawnLocation(origin.getBottomCenter(), Direction.UP));
            }
        }

        BlockPos adjacentPos = pos.relative(facing);
        if (allowsPortal(level, adjacentPos) && allowsPortal(level, adjacentPos.above())
                && allowsPortal(level, adjacentPos.below())) {
            return Optional.of(new PortalSpawnLocation(
                    adjacentPos.below().getBottomCenter().subtract(facing.getUnitVec3().scale(0.475)), facing));
        }
        return Optional.empty();
    }

    private boolean allowsPortal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir();
    }

}
