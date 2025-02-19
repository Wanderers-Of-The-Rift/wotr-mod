package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper for normal Minecraft item, as can be found in the CONTAINER data component or directly in a player's inventory
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
