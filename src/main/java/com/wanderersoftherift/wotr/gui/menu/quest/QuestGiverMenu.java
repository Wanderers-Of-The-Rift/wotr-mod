package com.wanderersoftherift.wotr.gui.menu.quest;

import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This menu presents the player with available quests that they can select. The quest list is replicated outside of the
 * menu.
 */
public class QuestGiverMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final List<QuestState> availableQuests;
    private boolean availableQuestsDirty = false;

    public QuestGiverMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL, new ArrayList<>());
    }

    public QuestGiverMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access,
            List<QuestState> availableQuests) {
        super(WotrMenuTypes.QUEST_GIVER_MENU.get(), containerId);

        this.access = access;
        this.availableQuests = availableQuests;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    public List<QuestState> getAvailableQuests() {
        return availableQuests;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, WotrBlocks.QUEST_HUB.get());
    }

    public void acceptQuest(ServerPlayer player, int index) {
        ActiveQuests activeQuests = player.getData(WotrAttachments.ACTIVE_QUESTS);
        if (activeQuests.isEmpty() && index >= 0 && index < availableQuests.size()) {
            activeQuests.add(availableQuests.get(index));
            player.closeContainer();
        }
    }

    public void setAvailableQuests(List<QuestState> quests) {
        this.availableQuests.clear();
        this.availableQuests.addAll(quests);
        availableQuestsDirty = true;
    }

    /**
     * @return Whether the available quests have changed and the screen should update
     */
    public boolean isDirty() {
        return availableQuestsDirty;
    }

    /**
     * Removes the dirty marker
     */
    public void clearDirty() {
        availableQuestsDirty = false;
    }
}
