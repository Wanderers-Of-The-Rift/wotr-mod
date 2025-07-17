package com.wanderersoftherift.wotr.item.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * BaseItemHandlerWrapper provides a base wrapper around an IItemHandler which can then be extended inorder to add
 * specific behaviors around an existing IItemHandler
 * <p>
 * It also provides a base implementation for {@link #setStackInSlot(int, ItemStack)} if the parent is not
 * {@link IItemHandlerModifiable}
 * </p>
 */
public abstract class BaseItemHandlerWrapper implements IItemHandlerModifiable {
    protected final IItemHandler parent;

    public BaseItemHandlerWrapper(IItemHandler parent) {
        this.parent = parent;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (parent instanceof IItemHandlerModifiable modifiableParent) {
            modifiableParent.setStackInSlot(slot, stack);
        } else {
            parent.extractItem(slot, parent.getStackInSlot(slot).getCount(), false);
            parent.insertItem(slot, stack, false);
        }
    }

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
        return parent.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return parent.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return parent.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return parent.isItemValid(slot, stack);
    }
}
