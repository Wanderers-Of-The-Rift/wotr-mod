package com.wanderersoftherift.wotr.core.inventory.containers;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper for directly working with an item stack for situations where that is appropriate.
 */
public class DirectContainerItemWrapper implements ContainerItemWrapper {

    private final ItemStack item;

    public DirectContainerItemWrapper(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getReadOnlyItemStack() {
        return item;
    }

    @Override
    public List<ItemStack> split(int amount) {
        return Collections.singletonList(item.split(amount));
    }

    @Override
    public List<ItemStack> remove() {
        return Collections.singletonList(item.copyAndClear());
    }

    @Override
    public void applyComponents(DataComponentPatch patch) {
        item.applyComponents(patch);
    }
}
