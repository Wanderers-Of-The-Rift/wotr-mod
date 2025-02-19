package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Interop for working with containers that use Neoforge's ItemStackHandler interface
 */
public final class ItemStackHandlerContainers {

    private ItemStackHandlerContainers() {

    }

    /**
     * @return An iterator over the non-empty contents of an ItemStackHandler
     */
    public static Iterator<ContainerItemWrapper> iterateNonEmpty(ItemStackHandler handler) {
        return new ItemStackHandlerNonEmptyIterator(handler);
    }

    /**
     * Iterator over the non-empty contents of an ItemStackHandler
     */
    public static class ItemStackHandlerNonEmptyIterator implements Iterator<ContainerItemWrapper> {

        private final ItemStackHandler handler;
        private int nextSlot = 0;
        private ContainerItemWrapper next;

        public ItemStackHandlerNonEmptyIterator(ItemStackHandler handler) {
            this.handler = handler;
            findNext();
        }

        private void findNext() {
            while (nextSlot < handler.getSlots() && handler.getStackInSlot(nextSlot).isEmpty()) {
                nextSlot++;
            }
            if (nextSlot >= handler.getSlots()) {
                next = null;
            } else {
                next = new ItemStackHandlerContainerItemWrapper(handler, nextSlot);
                nextSlot++;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public ContainerItemWrapper next() {
            ContainerItemWrapper result = next;
            findNext();
            return result;
        }
    }

    /**
     * Container Item implementation for items linked to an ItemStackHandler
     */
    public static class ItemStackHandlerContainerItemWrapper implements ContainerItemWrapper {
        private final ItemStackHandler handler;
        private final int slot;

        public ItemStackHandlerContainerItemWrapper(ItemStackHandler handler, int slot) {
            this.handler = handler;
            this.slot = slot;
        }

        @Override
        public ItemStack getReadOnlyItemStack() {
            return handler.getStackInSlot(slot);
        }

        @Override
        public List<ItemStack> split(int amount) {
            ItemStack existing = handler.getStackInSlot(slot);
            List<ItemStack> result = new ArrayList<>();
            while (amount > existing.getMaxStackSize()) {
                result.add(handler.extractItem(slot, existing.getMaxStackSize(), false));
                amount -= existing.getMaxStackSize();
            }
            result.add(handler.extractItem(slot, amount, false));
            return result;
        }

        @Override
        public List<ItemStack> remove() {
            List<ItemStack> result = new ArrayList<>();
            ItemStack existing = handler.getStackInSlot(slot);
            int amount = existing.getCount();
            while (amount > existing.getMaxStackSize()) {
                result.add(handler.extractItem(slot, existing.getMaxStackSize(), false));
                amount -= existing.getMaxStackSize();
            }
            result.add(handler.extractItem(slot, amount, false));
            return result;
        }

        @Override
        public void applyComponents(DataComponentPatch patch) {
            // Note: there can actually be multile of a non-stacking item within ItemStackHandler slot,
            // so pull them out one by one and apply the component patch to them before adding them all back in
            int amount = handler.getStackInSlot(slot).getCount();
            List<ItemStack> tagged = new ArrayList<>(amount);
            for (int i = 0; i < amount; i++) {
                ItemStack stack = handler.extractItem(slot, 1, false);
                stack.applyComponents(patch);
                tagged.add(stack);
            }
            for (ItemStack item : tagged) {
                handler.insertItem(slot, item, false);
            }
        }
    }
}


