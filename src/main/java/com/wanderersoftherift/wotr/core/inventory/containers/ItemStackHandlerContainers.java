package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Interop for working with containers that use Neoforge's ItemStackHandler interface. For use in producing container
 * types for mods
 */
public final class ItemStackHandlerContainers {

    private ItemStackHandlerContainers() {

    }

    /**
     * @return An iterator over the non-empty contents of an ItemStackHandler
     */
    public static Iterator<ItemAccessor> iterateNonEmpty(ItemStackHandler handler) {
        return new ItemStackHandlerNonEmptyIterator(handler);
    }

    /**
     * Iterator over the non-empty contents of an ItemStackHandler
     */
    public static class ItemStackHandlerNonEmptyIterator implements Iterator<ItemAccessor> {

        private final ItemStackHandler handler;
        private int nextSlot = 0;
        private ItemAccessor next;

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
                next = new ItemStackHandlerItemAccessor(handler, nextSlot);
                nextSlot++;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public ItemAccessor next() {
            ItemAccessor result = next;
            findNext();
            return result;
        }
    }

    /**
     * Container Item implementation for items linked to an ItemStackHandler
     */
    public static class ItemStackHandlerItemAccessor implements ItemAccessor {
        private final ItemStackHandler handler;
        private final int slot;
        private boolean modified;

        public ItemStackHandlerItemAccessor(ItemStackHandler handler, int slot) {
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
            modified = true;
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
            modified = true;
            return result;
        }

        @Override
        public void replace(ItemStack stack) {
            handler.setStackInSlot(slot, stack);
            modified = true;
        }

        @Override
        public void applyComponents(DataComponentPatch patch) {
            // Note: there can actually be multiple of a non-stacking item within ItemStackHandler slot,
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
            modified = true;
        }

        @Override
        public boolean isModified() {
            return modified;
        }
    }
}
