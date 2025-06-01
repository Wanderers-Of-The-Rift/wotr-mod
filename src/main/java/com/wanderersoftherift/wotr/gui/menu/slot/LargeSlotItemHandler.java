package com.wanderersoftherift.wotr.gui.menu.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * SlotItemHandler with support for storage beyond the standard item stack size
 */
public class LargeSlotItemHandler extends SlotItemHandler {

    public LargeSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return getItemHandler().getSlotLimit(this.index);
    }

}
