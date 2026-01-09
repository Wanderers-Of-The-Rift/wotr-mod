package com.wanderersoftherift.wotr.gui.menu.character;

import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

/**
 * A menu for displaying the player's standing with guilds they are affiliated with
 */
public class MainCharacterMenu extends BaseCharacterMenu {

    public MainCharacterMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public MainCharacterMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.MAIN_CHARACTER_MENU.get(), containerId);
    }

}
