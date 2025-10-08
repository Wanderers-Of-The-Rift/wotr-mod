package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.core.guild.trading.AvailableTrades;
import com.wanderersoftherift.wotr.core.guild.trading.Price;
import com.wanderersoftherift.wotr.gui.menu.slot.TakeAllOnlyItemHandlerSlot;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.item.handler.ChangeAwareItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Menu for trading
 */
public class TradingMenu extends AbstractContainerMenu {

    public static final int TRADE_SLOT_COLUMNS = 5;

    private static final QuickMover MOVER = QuickMover.create()
            .forPlayerSlots(AvailableTrades.MERCHANT_INVENTORY_SIZE)
            .forSlots(0, AvailableTrades.MERCHANT_INVENTORY_SIZE)
            .withTransform((stack) -> {
                stack.remove(WotrDataComponentType.PRICE);
                return stack;
            })
            .tryMoveToPlayer()
            .build();

    private final ValidatingLevelAccess access;

    private final IItemHandler merchantInventory;

    public TradingMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(AvailableTrades.MERCHANT_INVENTORY_SIZE),
                Minecraft.getInstance().player.getData(WotrAttachments.WALLET), ValidatingLevelAccess.NULL);
    }

    public TradingMenu(int containerId, Inventory playerInventory, IItemHandlerModifiable merchantInventory,
            Wallet wallet, ValidatingLevelAccess access) {
        super(WotrMenuTypes.TRADING_MENU.get(), containerId);
        this.access = access;
        this.merchantInventory = new ChangeAwareItemHandler(merchantInventory) {
            @Override
            public void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack) {
                if (oldStack.has(WotrDataComponentType.PRICE)) {
                    access.execute(
                            (level, pos) -> oldStack.get(WotrDataComponentType.PRICE).amounts().forEach(wallet::spend));
                }
            }
        };

        for (int i = 0; i < AvailableTrades.MERCHANT_INVENTORY_SIZE; i++) {
            int x = i % TRADE_SLOT_COLUMNS;
            int y = i / TRADE_SLOT_COLUMNS;

            this.addSlot(new TakeAllOnlyItemHandlerSlot(this.merchantInventory, i, 8 + 18 * x, 30 + 18 * y) {
                @Override
                public boolean mayPickup(Player playerIn) {
                    ItemStack item = getItem();
                    Price price = item.get(WotrDataComponentType.PRICE);
                    if (price != null && !price.canPay(playerIn)) {
                        return false;
                    }
                    return super.mayPickup(playerIn);
                }
            });
        }

        addStandardInventorySlots(playerInventory, 108, 84);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = MOVER.quickMove(this, player, index);
        // If the item partially moved, force move the rest
        if (index >= 0 && index < AvailableTrades.MERCHANT_INVENTORY_SIZE) {
            int remaining = merchantInventory.getStackInSlot(index).getCount();
            if (remaining > 0 && remaining < stack.getCount()) {
                ItemStack removed = merchantInventory.extractItem(index, remaining, false);
                if (getCarried().getCount() == 0) {
                    setCarried(removed);
                } else {
                    player.drop(removed, false);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return access.isValid(player);
    }

    public boolean canPurchase(int slot, Player player) {
        Price price = merchantInventory.getStackInSlot(slot).get(WotrDataComponentType.PRICE);
        if (price != null) {
            return price.canPay(player);
        }
        return true;
    }
}
