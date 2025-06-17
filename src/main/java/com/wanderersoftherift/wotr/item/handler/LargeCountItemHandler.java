package com.wanderersoftherift.wotr.item.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

/**
 * An item handler for holding large amounts of a single item
 */
public class LargeCountItemHandler implements IItemHandlerModifiable {

    private final ItemStack itemType;
    private final int maxCount;
    private int count = 0;
    private Runnable listener = () -> {
    };

    public LargeCountItemHandler(ItemStack itemType, int maxCount) {
        this.maxCount = maxCount;
        this.itemType = itemType;
    }

    /**
     * @return The amount of the item currently held
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The amount of the item being held
     */
    public void setCount(int count) {
        this.count = Math.clamp(count, 0, maxCount);
        listener.run();
    }

    /**
     * @param listener The listener to be executed on change
     */
    public void registerChangeListener(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (count == 0) {
            return ItemStack.EMPTY;
        }
        return itemType.copyWithCount(count);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            count = 0;
        } else {
            count = stack.getCount();
        }
        listener.run();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }

        int limit = maxCount - count;
        if (limit <= 0) {
            return stack;
        }

        if (!simulate) {
            count = Math.min(count + stack.getCount(), maxCount);
            listener.run();
        }

        if (stack.getCount() > limit) {
            return stack.copyWithCount(stack.getCount() - limit);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || count == 0) {
            return ItemStack.EMPTY;
        }
        int returnAmount = Math.min(Math.min(amount, count), itemType.getMaxStackSize());
        if (!simulate) {
            count -= returnAmount;
            listener.run();
        }
        return itemType.copyWithCount(returnAmount);
    }

    @Override
    public int getSlotLimit(int slot) {
        return maxCount;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return ItemStack.isSameItemSameComponents(itemType, stack);
    }
}
