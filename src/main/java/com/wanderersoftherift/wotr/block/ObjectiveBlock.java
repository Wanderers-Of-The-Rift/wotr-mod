package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.core.goal.GoalEvent;
import com.wanderersoftherift.wotr.core.goal.type.ActivateObjectiveGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class ObjectiveBlock extends Block {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public ObjectiveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(ACTIVATED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide && !state.getValue(ACTIVATED)) {
            GoalEvent.Update<ActivateObjectiveGoal> event = new GoalEvent.Update<>(player, ActivateObjectiveGoal.class,
                    goal -> 1);
            NeoForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                level.setBlock(pos, state.cycle(ACTIVATED), Block.UPDATE_ALL);
                // playSound(player, level, pos, state);
            } else {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.SUCCESS;
    }
}
