package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Interface for working with items from containers in a player's inventory. This is tailored specifically for Inventory Snapshot usage.
 */
public interface ContainerItemWrapper {

    /**
     * @return The item stack for the container item, for read only use
     */
    ItemStack getReadOnlyItemStack();

    /**
     * Splits an amount out of the container item, up to and including the full amount
     * @param amount The amount to split out
     * @return The one or more item stacks split out of the container's item stack, each with size up to maxItemStackSize
     */
    List<ItemStack> split(int amount);

    /**
     * @return Removes an item from the container.
     */
    List<ItemStack> remove();

    /**
     * Applies a component patch to the container item
     * @param patch A data component change (addition/removal/combination)
     */
    void applyComponents(DataComponentPatch patch);

}
