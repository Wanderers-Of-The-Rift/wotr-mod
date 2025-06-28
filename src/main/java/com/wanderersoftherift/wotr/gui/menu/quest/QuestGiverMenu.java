package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.core.guild.quest.Quest;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.network.guild.ActiveQuestReplicationPayload;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        List<ActiveQuest> activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS).quests();
        if (activeQuests.isEmpty()) {
            ActiveQuest activeQuest = new ActiveQuest(quest);
            activeQuests.add(activeQuest);
            PacketDistributor.sendToPlayer(player, new ActiveQuestReplicationPayload(activeQuests));
            // TODO: open completion screen
            player.closeContainer();
        }
    }
}
