package com.wanderersoftherift.wotr.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class ItemStackHandlerUtil {
    private ItemStackHandlerUtil() {

    }

    public static void placeInPlayerInventoryOrDrop(ServerPlayer player, ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            // TODO: may need extra logic to handle oversized stacks
            placeInPlayerInventoryOrDrop(player, handler.getStackInSlot(i).copy());
        }
    }

    public static void placeInPlayerInventoryOrDrop(ServerPlayer player, ItemStack stack) {
        if (!stack.isEmpty()) {
            if (player.isRemoved() || player.hasDisconnected()) {
                player.drop(stack, false);
            } else {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }
}
