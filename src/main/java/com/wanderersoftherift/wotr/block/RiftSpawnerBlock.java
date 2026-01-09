package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.RiftSpawnerBlockEntity;
import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.entity.portal.RiftPortalEntranceEntity;
import com.wanderersoftherift.wotr.util.VoxelShapeUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

/**
 * A Rift Spawner block is a usable block for generating rift entrances.
 * <p>
 * Rift Spawner is a multi-block structure. The lower half-block controls the structure. If any block is removed then
 * all the blocks are removed.
 * </p>
 * <p>
 * If there is an active portal when the spawner is removed, then the portal is destroyed.
 * </p>
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<RiftSpawnerBlock> CODEC = simpleCodec(RiftSpawnerBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape BASE_LOWER_SHAPE = VoxelShapeUtils.combine(
            Block.box(0, 0, 1.5, 16, 2, 14.5), Block.box(2, 2, 3.5, 14, 4, 12.5), Block.box(6, 4, 5.5, 10, 6, 10.5),
            Block.box(4, 6, 5, 12, 9, 10), Block.box(1.25, 8.25, 6, 4, 11.25, 9),
            Block.box(12, 8.25, 6, 14.75, 11.25, 9), Block.box(-1.5, 10.25, 6, 1.25, 13.25, 9),
            Block.box(14.75, 10.25, 6, 17.5, 13.25, 9), Block.box(-3, 13.25, 5.25, 1, 16, 10.25),
            Block.box(15, 13.25, 5.25, 19, 16, 10.25));
    private static final VoxelShape BASE_UPPER_SHAPE = VoxelShapeUtils.combine(
            Block.box(-3, 0, 5.25, 1, 5.25, 10.25), Block.box(15, 0, 5.25, 19, 5.25, 10.25),
            Block.box(-1.5, 5.25, 6, 1.25, 8.25, 9), Block.box(14.75, 5.25, 6, 17.5, 8.25, 9),
            Block.box(1.25, 7.25, 6, 4, 10.25, 9), Block.box(12, 7.25, 6, 14.75, 10.25, 9),
            Block.box(4, 10, 5.5, 12, 13, 9.5)
    );
    private static final Table<DoubleBlockHalf, Direction.Axis, VoxelShape> SHAPES;

    static {
        SHAPES = ImmutableTable.<DoubleBlockHalf, Direction.Axis, VoxelShape>builder()
                .put(DoubleBlockHalf.LOWER, Direction.Axis.Z, BASE_LOWER_SHAPE)
                .put(DoubleBlockHalf.LOWER, Direction.Axis.X,
                        VoxelShapeUtils.rotateHorizontal(BASE_LOWER_SHAPE, Direction.EAST))
                .put(DoubleBlockHalf.UPPER, Direction.Axis.Z, BASE_UPPER_SHAPE)
                .put(DoubleBlockHalf.UPPER, Direction.Axis.X,
                        VoxelShapeUtils.rotateHorizontal(BASE_UPPER_SHAPE, Direction.EAST))
                .build();
    }

    public RiftSpawnerBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    public MapCodec<RiftSpawnerBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF),
                state.getValue(HorizontalDirectionalBlock.FACING).getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(HALF);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        getSpawnLocation(level, pos, state).ifPresent(loc -> getExistingRifts(level, loc.position().add(0, 0.1, 0))
                .forEach(rift -> rift.remove(Entity.RemovalReason.KILLED)));
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduler,
            BlockPos pos,
            Direction dir,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (dir.getAxis() == Direction.Axis.Y && (doubleBlockHalf == DoubleBlockHalf.LOWER) == (dir == Direction.UP)) {
            if (adjState.getBlock() instanceof RiftSpawnerBlock && adjState.getValue(HALF) != doubleBlockHalf) {
                return adjState.setValue(HALF, doubleBlockHalf);
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return state;
    }

    public static List<RiftPortalEntranceEntity> getExistingRifts(Level level, Vec3 pos) {
        return level.getEntities(EntityTypeTest.forClass(RiftPortalEntranceEntity.class),
                new AABB(BlockPos.containing(pos)), x -> true);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        Direction direction = context.getHorizontalDirection().getOpposite();
        if (blockpos.getY() < level.getMaxY() && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, direction).setValue(HALF, DoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity player,
            ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
            if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                    level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, blockpos, Block.getId(blockstate));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Provides the location to create the rift, if a valid location exists
     *
     * @param level
     * @param pos
     * @return A valid spawn location, or Optional#empty
     */
    public Optional<PortalSpawnLocation> getSpawnLocation(Level level, BlockPos pos, BlockState state) {
        DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);

        if (half == DoubleBlockHalf.LOWER) {
            return Optional.of(new PortalSpawnLocation(pos.getBottomCenter().add(0, 0.525, 0),
                    state.getValue(HorizontalDirectionalBlock.FACING)));
        } else {
            return Optional.of(new PortalSpawnLocation(pos.below().getBottomCenter().add(0, 0.525, 0),
                    state.getValue(HorizontalDirectionalBlock.FACING)));
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftSpawnerBlockEntity(pos, state);
    }
}
