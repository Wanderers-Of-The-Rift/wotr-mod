package com.wanderersoftherift.wotr.item.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * ItemHandler wrapper that allows definition of an onSlotChanged method.
 */
public abstract class ChangeAwareItemHandler implements IItemHandlerModifiable {
    private final IItemHandlerModifiable parent;

    public ChangeAwareItemHandler(IItemHandlerModifiable parent) {
        this.parent = parent;
    }

    public abstract void onSlotChanged(int slot, ItemStack oldStack, ItemStack newStack);

    @Override
    public int getSlots() {
        return parent.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return parent.getStackInSlot(slot);
    }

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

    @Override
    public int getSlotLimit(int slot) {
        return parent.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return parent.isItemValid(slot, stack);
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        ItemStack original = parent.getStackInSlot(slot).copy();
        parent.setStackInSlot(slot, stack);
        onSlotChanged(slot, original, stack);
    }
}
