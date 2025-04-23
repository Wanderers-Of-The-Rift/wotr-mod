package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.gui.menu.AbilityBenchMenu;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.item.handler.ChangeAwareItemHandler;
import com.wanderersoftherift.wotr.network.AbilitySlotsUpdatePayload;
import com.wanderersoftherift.wotr.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

/**
 * Block for the workbench allowing ability assignment and upgrade
 */
public class AbilityBenchBlock extends Block {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final Component CONTAINER_TITLE = Component
            .translatable("container." + WanderersOfTheRift.MODID + ".ability_bench");
    private static final VoxelShape SHAPE = VoxelShapeUtils.combine(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0),
            Block.box(2.0, 1.0, 2.0, 14.0, 12.0, 14.0), Block.box(0.0, 12.0, 0.0, 16.0, 15.0, 16.0));

    public AbilityBenchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
            @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    protected MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) -> {
            AbilitySlots slots = playerInventory.player.getData(ModAttachments.ABILITY_SLOTS);
            IItemHandler replicatedSlots = new ChangeAwareItemHandler(slots) {
                @Override
                public void onSlotChanged(int slot) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player,
                            new AbilitySlotsUpdatePayload(slot, slots.getStackInSlot(slot)));
                }
            };
            return new AbilityBenchMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos),
                    replicatedSlots);
        }, CONTAINER_TITLE);
    }

    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
            @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }
}
