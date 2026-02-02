package com.wanderersoftherift.wotr.core.inventory;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Interface for working with items from containers in a player's inventory. This is primarily tailored for Inventory
 * Snapshot usage.
 */
public interface ItemAccessor {

    /**
     * Many containers do not support directly modifying items. Item stacks retrieved from the accessor should not be
     * modified directly, and instead the modifier methods below should be used.
     * 
     * @return The item stack for the container item, for read only use.
     */
    ItemStack getReadOnlyItemStack();

    /**
     * @return Whether the item has been modified
     */
    boolean isModified();

    /**
     * Splits an amount out of the container item, up to and including the full amount
     * 
     * @param amount The amount to split out
     * @return The one or more item stacks split out of the container's item stack, each with size up to
     *         maxItemStackSize
     */
    List<ItemStack> split(int amount);

    /**
     * @return Removes an item from the container.
     */
    List<ItemStack> remove();

    /**
     * Replaces the content with a new stack
     * 
     * @param stack
     */
    void replace(ItemStack stack);

    /**
     * Applies a component patch to the container item
     * 
     * @param patch A data component change (addition/removal/combination)
     */
    void applyComponents(DataComponentPatch patch);

}
