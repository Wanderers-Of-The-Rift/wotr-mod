package com.wanderersoftherift.wotr.gui.menu.character;

import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

/**
 * A menu for displaying the player's standing with guilds they are affiliated with
 */
public class GuildMenu extends BaseCharacterMenu {

    public GuildMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public GuildMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.GUILDS_MENU.get(), containerId);
    }

}
