package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.gui.menu.QuickMover;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.item.handler.QuestItemStackHandler;
import com.wanderersoftherift.wotr.network.quest.QuestRewardsPayload;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Menu for handing in items and completing a quest with all goals met. It includes:
 * <ul>
 * <li>A data slot with the index of the quest being completed</li>
 * <li>A Hand-in slot that accepts items that are required to complete the quest (if any)</li>
 * <li>The standard player inventory slots</li>
 * </ul>
 * <p>
 * Items that are handed in are added to the progress of relevant quest goals. Excess is returned to the player.
 * </p>
 */
public class QuestCompletionMenu extends AbstractContainerMenu {
    private static final int HAND_IN_SLOTS = 1;
    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(HAND_IN_SLOTS)
            .tryMoveTo(0, HAND_IN_SLOTS)
            .forSlots(0, HAND_IN_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ContainerLevelAccess access;
    private final DataSlot selectedQuest;
    private final ItemStackHandler handInItems;
    private final ActiveQuests quests;

    public QuestCompletionMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL,
                Minecraft.getInstance().player.getData(WotrAttachments.ACTIVE_QUESTS), 0);
    }

    public QuestCompletionMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access,
            ActiveQuests activeQuests, int questIndex) {
        super(WotrMenuTypes.QUEST_COMPLETION_MENU.get(), containerId);

        this.access = access;
        this.quests = activeQuests;
        this.selectedQuest = DataSlot.standalone();
        selectedQuest.set(questIndex);
        addDataSlot(selectedQuest);
        handInItems = new QuestItemStackHandler(() -> quests.getQuestState(selectedQuest.get()), HAND_IN_SLOTS);
        for (int slot = 0; slot < HAND_IN_SLOTS; slot++) {
            addSlot(new SlotItemHandler(handInItems, slot, 162 + slot * 18, 32));
        }
        addStandardInventorySlots(playerInventory, 158, 118);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.QUEST_HUB.get());
    }

    public QuestState getQuestState() {
        return quests.getQuestState(selectedQuest.get());
    }

    public boolean hasItemToHandIn() {
        return !handInItems.getStackInSlot(0).isEmpty();
    }

    public void handIn(ServerPlayer player) {
        access.execute((level, blockPos) -> {
            QuestState quest = getQuestState();
            for (int index = 0; index < quest.goalCount(); index++) {
                if (!quest.isGoalComplete(index)
                        && quest.getGoal(index) instanceof GiveItemGoal(Ingredient item, int count)) {
                    for (int slot = 0; slot < HAND_IN_SLOTS; slot++) {
                        ItemStack itemsToHandIn = handInItems.getStackInSlot(slot);
                        if (itemsToHandIn.isEmpty()) {
                            continue;
                        }

                        if (item.test(itemsToHandIn)) {
                            int remainingItems = count - quest.getGoalProgress(index);
                            if (itemsToHandIn.getCount() >= remainingItems) {
                                quest.setGoalProgress(index, quest.getGoalProgress(index) + remainingItems);
                                handInItems.extractItem(slot, remainingItems, false);
                            } else {
                                quest.setGoalProgress(index, quest.getGoalProgress(index) + itemsToHandIn.getCount());
                                handInItems.extractItem(slot, itemsToHandIn.getCount(), false);
                            }
                        }
                    }
                }
            }
            // Empty the slots of residual items
            for (int slot = 0; slot < HAND_IN_SLOTS; slot++) {
                quickMoveStack(player, slot);
            }
        });
    }

    public void completeQuest(ServerPlayer player, UUID questId) {
        QuestState questState = getQuestState();
        if (!questState.getId().equals(questId) || !questState.isComplete()
                || !player.getData(WotrAttachments.ACTIVE_QUESTS).remove(questState.getId())) {
            return;
        }

        // Remove the quest so the player cannot take it again, if it is still available
        player.getData(WotrAttachments.AVAILABLE_QUESTS).removeIf(x -> x.getId().equals(questState.getId()));
        player.closeContainer();

        List<Reward> rewards = questState.getRewards();
        access.execute((level, blockPos) -> {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> {
                        var menu = new QuestRewardMenu(containerId, playerInventory,
                                ContainerLevelAccess.create(level, p.getOnPos()));
                        menu.setRewards(p, rewards);
                        return menu;
                    }, Component.translatable(WanderersOfTheRift.translationId("container", "quest_complete"))));
        });
        PacketDistributor.sendToPlayer(player,
                new QuestRewardsPayload(rewards.stream().filter(x -> !x.isItem()).toList()));
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(serverPlayer, handInItems);
        }
    }
}
