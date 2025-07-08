package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.core.guild.quest.QuestState;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class QuestGiverMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public QuestGiverMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public QuestGiverMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.QUEST_GIVER_MENU.get(), containerId);

        this.access = access;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.QUEST_HUB.get());
    }

    public void acceptQuest(ServerPlayer player, Holder<Quest> quest) {
        // TODO: check quest is valid in this context
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty()) {
            QuestState questState = new QuestState(quest);
            activeQuests.add(questState);
            player.closeContainer();
        }
    }
}
