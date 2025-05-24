package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.core.guild.trading.TradeOffering;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TradingMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;
    private final ItemStackHandler purchaseItem;
    private final SlotItemHandler purchaseSlot;

    public TradingMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public TradingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WotrMenuTypes.TRADING_MENU.get(), containerId);
        this.access = access;
        this.purchaseItem = new ItemStackHandler(1);
        purchaseSlot = new SlotItemHandler(purchaseItem, 0, 150, 123);
        this.addSlot(purchaseSlot);

        addStandardInventorySlots(playerInventory, 108, 84);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        // TODO
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        // TODO: Distance check menu source
        return access.evaluate((level, pos) -> true, true);
    }

    public void selectTrade(Holder<TradeOffering> trade) {

    }
}
