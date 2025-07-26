package com.wanderersoftherift.wotr.gui.menu.character;

import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

/**
 * A menu displaying the status of all quests a player has accepted
 */
public class QuestMenu extends BaseCharacterMenu {
    public QuestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public QuestMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.QUEST_MENU.get(), containerId);
    }
}
