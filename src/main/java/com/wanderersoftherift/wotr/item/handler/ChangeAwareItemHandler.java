package com.wanderersoftherift.wotr.item.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * ItemHandler wrapper that allows definition of an onSlotChanged method.
 */
public abstract class ChangeAwareItemHandler extends BaseItemHandlerWrapper {

    public ChangeAwareItemHandler(IItemHandlerModifiable parent) {
        super(parent);
    }

    public abstract void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack);

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack original = parent.getStackInSlot(slot).copy();
        ItemStack result = parent.insertItem(slot, stack, simulate);
        if (!simulate && !result.equals(stack)) {
            onSlotChanged(slot, original, parent.getStackInSlot(slot));
        }
        return result;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack original = parent.getStackInSlot(slot).copy();
        ItemStack result = parent.extractItem(slot, amount, simulate);
        if (!simulate && !result.isEmpty()) {
            onSlotChanged(slot, original, parent.getStackInSlot(slot));
        }
        return result;
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (parent instanceof IItemHandlerModifiable modifiableParent) {
            ItemStack original = parent.getStackInSlot(slot).copy();
            modifiableParent.setStackInSlot(slot, stack);
            onSlotChanged(slot, original, stack);
        } else {
            super.setStackInSlot(slot, stack);
        }
    }
}
