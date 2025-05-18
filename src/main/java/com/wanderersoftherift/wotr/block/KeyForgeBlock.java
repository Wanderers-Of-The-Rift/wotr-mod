package com.wanderersoftherift.wotr.block;

import com.google.common.collect.ImmutableMap;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.KeyForgeMenu;
import com.wanderersoftherift.wotr.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A crafting bench for Rift Keys
 */
public class KeyForgeBlock extends Block {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final Component CONTAINER_TITLE = Component
            .translatable("container." + WanderersOfTheRift.MODID + ".key_forge");

    private static final VoxelShape SHAPE = VoxelShapeUtils.combine(
            Block.box(3.0, 0.0, 1.0, 13.0, 9.0, 15.0), Block.box(5.0, 9.0, 1.0, 11.0, 14.0, 5.0)
    );

    private static final Map<Direction, VoxelShape> SHAPES;

    static {
        ImmutableMap.Builder<Direction, VoxelShape> builder = ImmutableMap.builder();
        builder.put(Direction.NORTH, SHAPE);
        builder.put(Direction.EAST, VoxelShapeUtils.rotateHorizontal(SHAPE, Direction.EAST));
        builder.put(Direction.SOUTH, VoxelShapeUtils.rotateHorizontal(SHAPE, Direction.SOUTH));
        builder.put(Direction.WEST, VoxelShapeUtils.rotateHorizontal(SHAPE, Direction.WEST));
        SHAPES = builder.build();
    }

    public KeyForgeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
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

    protected MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> new KeyForgeMenu(containerId,
                playerInventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
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
}
