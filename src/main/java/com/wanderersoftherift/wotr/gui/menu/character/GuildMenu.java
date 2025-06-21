package com.wanderersoftherift.wotr.gui.menu.character;

import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class GuildMenu extends BaseCharacterMenu {

    public GuildMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public GuildMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.GUILDS_MENU.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
