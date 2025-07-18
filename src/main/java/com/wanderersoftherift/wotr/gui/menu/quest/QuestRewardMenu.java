package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.core.guild.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.QuickMover;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.util.ItemStackHandlerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestRewardMenu extends AbstractContainerMenu {

    private static final int NUM_REWARD_SLOTS = 9;
    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(NUM_REWARD_SLOTS)
            .forSlots(0, NUM_REWARD_SLOTS)
            .tryMoveToPlayer()
            .build();

    private final ContainerLevelAccess access;
    private final ActiveQuests activeQuests;
    private final DataSlot selectedQuest;

    private final ItemStackHandler rewardItems;

    public QuestRewardMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL,
                Minecraft.getInstance().player.getData(WotrAttachments.ACTIVE_QUESTS), 0);
    }

    public QuestRewardMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access,
            ActiveQuests activeQuests, int questIndex) {
        super(WotrMenuTypes.QUEST_REWARD_MENU.get(), containerId);

        this.access = access;
        this.activeQuests = activeQuests;
        this.rewardItems = new ItemStackHandler(NUM_REWARD_SLOTS);

        this.selectedQuest = addDataSlot(DataSlot.standalone());
        selectedQuest.set(questIndex);

        for (int i = 0; i < NUM_REWARD_SLOTS; i++) {
            this.addSlot(new SlotItemHandler(rewardItems, i, 8 + 18 * i, 49));
        }
        addStandardInventorySlots(playerInventory, 8, 80);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return MOVER.quickMove(this, player, index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public List<Reward> getNonItemRewards() {
        return activeQuests.getQuestState(selectedQuest.get()).getRewards().stream().filter(x -> !x.isItem()).toList();
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        QuestState questState = activeQuests.getQuestState(selectedQuest.get());
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStackHandlerUtil.placeInPlayerInventoryOrDrop(serverPlayer, rewardItems);
            List<Reward> rewards = questState.getRewards();
            for (Reward reward : rewards) {
                reward.apply(serverPlayer);
            }
            activeQuests.remove(questState.getId());
        }
    }

    public void addRewards(ServerPlayer player) {
        access.execute((level, pos) -> {
            QuestState quest = activeQuests.getQuestState(selectedQuest.get());
            for (Reward reward : quest.getRewards()) {
                ItemStack rewardItem = reward.generateItem();

                if (!rewardItem.isEmpty()) {
                    ItemStackHandlerUtil.addOrGiveToPlayerOrDrop(rewardItem, rewardItems, player);
                }
            }
        });
    }
}
