package com.wanderersoftherift.wotr.gui.menu.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TakeAllOnlyItemHandlerSlot extends SlotItemHandler {
    public TakeAllOnlyItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        if (amount != getItem().getCount()) {
            return ItemStack.EMPTY;
        }
        return super.remove(amount);
    }
}
