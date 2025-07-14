package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.core.guild.currency.Currency;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.core.guild.trading.TradeListing;
import com.wanderersoftherift.wotr.gui.menu.slot.TakeAllOnlyItemHandlerSlot;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrMenuTypes;
import com.wanderersoftherift.wotr.item.handler.ChangeAwareItemHandler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
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

    private static final QuickMover MOVER = QuickMover.create().forPlayerSlots(1).forSlot(0).tryMoveToPlayer().build();

    private final ContainerLevelAccess access;
    private final ChangeAwareItemHandler purchaseItem;
    private final SlotItemHandler purchaseSlot;
    private final Wallet wallet;

    private Holder<TradeListing> currentTrade;
    private boolean updatingTrade = false;

    public TradingMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL,
                Minecraft.getInstance().player.getData(WotrAttachments.WALLET));
    }

    public TradingMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, Wallet wallet) {
        super(WotrMenuTypes.TRADING_MENU.get(), containerId);
        this.access = access;
        this.wallet = wallet;
        this.purchaseItem = new ChangeAwareItemHandler(new ItemStackHandler(1)) {
            @Override
            public void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack) {
                access.execute((level, pos) -> {
                    if (!updatingTrade && currentTrade != null) {
                        for (Object2IntMap.Entry<Holder<Currency>> costElement : currentTrade.value()
                                .getPrice()
                                .object2IntEntrySet()) {
                            wallet.spend(costElement.getKey(), costElement.getIntValue());
                        }
                        updateTradeSlot();
                    }
                });
            }
        };
        purchaseSlot = new TakeAllOnlyItemHandlerSlot(purchaseItem, 0, 112, 22);
        this.addSlot(purchaseSlot);

        addStandardInventorySlots(playerInventory, 108, 84);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = MOVER.quickMove(this, player, index);
        // If quick move has left residual, move it to the player's hand or drop it (alternatively we could take a bit
        // back)
        int remaining = purchaseItem.getStackInSlot(0).getCount();
        if (currentTrade != null && remaining > 0 && remaining < currentTrade.value().getOutputItem().getCount()) {
            ItemStack residual = purchaseItem.extractItem(0, remaining, false);
            if (getCarried().getCount() == 0) {
                setCarried(residual);
            } else {
                player.drop(residual, false);
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        // TODO: Distance check menu source
        return access.evaluate((level, pos) -> true, true);
    }

    public void selectTrade(Holder<TradeListing> trade) {
        if (trade == null) {
            return;
        }
        currentTrade = trade;
        updateTradeSlot();
    }

    private void updateTradeSlot() {
        updatingTrade = true;
        if (canPay(currentTrade.value().getPrice())) {
            purchaseItem.setStackInSlot(0, currentTrade.value().getOutputItem().copy());
        } else {
            purchaseItem.setStackInSlot(0, ItemStack.EMPTY);
        }
        updatingTrade = false;
    }

    private boolean canPay(Object2IntMap<Holder<Currency>> price) {
        return price.object2IntEntrySet()
                .stream()
                .map(currencyCost -> wallet.get(currencyCost.getKey()) >= currencyCost.getIntValue())
                .reduce(true, (x, y) -> x && y);
    }
}
