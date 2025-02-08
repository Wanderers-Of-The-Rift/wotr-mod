package com.dimensiondelvers.dimensiondelvers.gui.menu;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AbilityBagMenu extends AbstractContainerMenu {
    //client

    private Inventory playerInventory;
    private Container bagInventory;

    //Server
    public AbilityBagMenu(int containerId, Inventory playerInventory, Container bagInventory) {
        super(ModMenuTypes.ABILITY_BAG_MENU.get(),containerId);
        this.playerInventory = playerInventory;
        this.bagInventory = bagInventory;
        this.createInventorySlots(this.playerInventory);
    }
    public AbilityBagMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.ABILITY_BAG_MENU.get(),containerId);
        this.playerInventory = playerInventory;
        this.createInventorySlots(this.playerInventory);
        if (bagInventory == null) {
            DimensionDelvers.LOGGER.info("null inventory");
            return;
        }
        this.createBagSlots(bagInventory);


    }

    private void createInventorySlots(Inventory inventory) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18));
            }
        }
        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 224));
        }
    }
    private void createBagSlots(Container bagInventory) {
        for (int i = 0; i<9; i++) {
            this.addSlot(new Slot(bagInventory, i, 8+i * 18, 500));
        }
    }


    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }




}
