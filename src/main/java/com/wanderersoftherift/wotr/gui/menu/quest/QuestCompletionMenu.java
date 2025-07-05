package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.gui.menu.QuickMover;
import com.wanderersoftherift.wotr.gui.menu.slot.QuestItemStackHandler;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.network.guild.ActiveQuestReplicationPayload;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

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
        handInItems = new QuestItemStackHandler(() -> quests.quests().get(selectedQuest.get()), HAND_IN_SLOTS);
        for (int slot = 0; slot < HAND_IN_SLOTS; slot++) {
            addSlot(new SlotItemHandler(handInItems, slot, 112 + slot * 18, 32));
        }
        addStandardInventorySlots(playerInventory, 108, 118);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.QUEST_HUB.get());
    }

    public ActiveQuest getQuest() {
        return quests.quests().get(selectedQuest.get());
    }

    public boolean hasItemToHandIn() {
        return !handInItems.getStackInSlot(0).isEmpty();
    }

    public void handIn(ServerPlayer player) {
        access.execute((level, blockPos) -> {
            ActiveQuest quest = getQuest();
            boolean changed = false;
            for (int index = 0; index < quest.goalCount(); index++) {
                if (!quest.isGoalComplete(index) && quest.getGoal(index) instanceof GiveItemGoal goal) {
                    for (int slot = 0; slot < HAND_IN_SLOTS; slot++) {
                        ItemStack itemsToHandIn = handInItems.getStackInSlot(slot);
                        if (itemsToHandIn.isEmpty()) {
                            continue;
                        }

                        if (goal.item().test(itemsToHandIn)) {
                            changed = true;
                            int remainingItems = goal.progressTarget() - quest.getGoalProgress(index);
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
            // TODO: replicate specific changed quest.
            if (changed) {
                PacketDistributor.sendToPlayer(player, new ActiveQuestReplicationPayload(quests.quests()));
            }
            // Empty the slots of residual items
            for (int slot = 0; slot < HAND_IN_SLOTS; slot++) {
                quickMoveStack(player, slot);
            }
        });
    }

    public void completeQuest(ServerPlayer player, int questIndex) {
        if (questIndex != selectedQuest.get() || !getQuest().isComplete()) {
            return;
        }
        access.execute((level, blockPos) -> {
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(player, handInItems);
            player.closeContainer();
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> {
                        var menu = new QuestRewardMenu(containerId, playerInventory,
                                ContainerLevelAccess.create(level, p.getOnPos()), quests, selectedQuest.get());
                        menu.addRewards(player);
                        return menu;
                    }, Component.translatable(WanderersOfTheRift.translationId("container", "quest_complete"))));
        });
    }

    public int getQuestIndex() {
        return selectedQuest.get();
    }
}
