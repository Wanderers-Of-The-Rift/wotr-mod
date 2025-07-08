package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
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
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty()) {
            player.openMenu(new SimpleMenuProvider((containerId, playerInventory, p) -> new QuestGiverMenu(containerId,
                    playerInventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE));
        } else {
            player.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId, playerInventory,
                                    ContainerLevelAccess.create(level, pos), p.getData(WotrAttachments.ACTIVE_QUESTS),
                                    0),
                            Quest.title(activeQuests.getQuestState(0).getQuest())));
        }
        return InteractionResult.CONSUME;
    }
}
