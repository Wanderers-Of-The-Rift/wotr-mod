package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.QuestMenuHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * A block that provides access to selecting and completing quests
 */
public class QuestHubBlock extends Block {

    public static final Component CONTAINER_TITLE = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.selection"));

    public QuestHubBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        QuestMenuHelper.openQuestMenu(serverPlayer, CONTAINER_TITLE, pos, this);
        return InteractionResult.CONSUME;
    }

}
