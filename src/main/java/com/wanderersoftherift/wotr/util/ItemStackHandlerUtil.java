package com.wanderersoftherift.wotr.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Utility methods for working with an item stack handler
 */
public final class ItemStackHandlerUtil {
    private ItemStackHandlerUtil() {

    }

    /**
     * Attempts to place all the items in the item stack handler into the player's inventory. Remaining items are
     * dropped at the player's location.
     *
     * @param player
     * @param handler
     */
    public static void placeInPlayerInventoryOrDrop(ServerPlayer player, ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            // TODO: may need extra logic to handle oversized stacks
            placeInPlayerInventoryOrDrop(player, handler.getStackInSlot(i).copy());
        }
    }

    /**
     * Attempts to place the item stack into the player's inventory. If some of the stack remains it is dropped at the
     * player's location.
     *
     * @param player
     * @param stack
     */
    public static void placeInPlayerInventoryOrDrop(ServerPlayer player, ItemStack stack) {
        if (!stack.isEmpty()) {
            if (player.isRemoved() || player.hasDisconnected()) {
                player.drop(stack, false);
            } else {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    /**
     * Attempts to add a stack into an {@link ItemStackHandler}. If it doesn't fit or there is residual, attempts to add
     * it to a player's inventory before dropping at the player's location.
     *
     * @param item
     * @param handler
     * @param player
     */
    public static void addOrGiveToPlayerOrDrop(ItemStack item, ItemStackHandler handler, ServerPlayer player) {
        ItemStack residual = item;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) {
                residual = handler.insertItem(i, residual, false);
                if (residual.isEmpty()) {
                    return;
                }
            }
        }
        if (!residual.isEmpty()) {
            if (player.isRemoved() || player.hasDisconnected()) {
                player.drop(item, false);
            } else {
                player.getInventory().placeItemBackInInventory(item);
            }
        }
    }
}
