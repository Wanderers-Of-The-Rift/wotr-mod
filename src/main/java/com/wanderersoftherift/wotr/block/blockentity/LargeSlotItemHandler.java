package com.wanderersoftherift.wotr.block.blockentity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LargeSlotItemHandler extends SlotItemHandler {

    public LargeSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return getItemHandler().getSlotLimit(this.index);
    }

}
