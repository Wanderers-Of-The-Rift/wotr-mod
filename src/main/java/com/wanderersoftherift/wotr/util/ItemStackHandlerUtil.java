package com.wanderersoftherift.wotr.util;

import com.wanderersoftherift.wotr.loot.InstantLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
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
    public static void placeInPlayerInventoryOrDrop(Player player, IItemHandler handler) {
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
    public static void placeInPlayerInventoryOrDrop(Player player, ItemStack stack) {
        if (InstantLoot.tryConsume(stack, player) || stack.isEmpty()) {
            return;
        }
        if (player.isRemoved() || (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected())) {
            player.drop(stack, false);
        } else {
            player.getInventory().placeItemBackInInventory(stack);
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
    public static void addOrGiveToPlayerOrDrop(ItemStack item, IItemHandler handler, Player player) {
        ItemStack residual = item;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) {
                residual = handler.insertItem(i, residual, false);
                if (residual.isEmpty()) {
                    return;
                }
            }
        }
        placeInPlayerInventoryOrDrop(player, residual);
    }

    /**
     * Drops the contents of the IItemHandler
     * 
     * @param level       The level to drop the contents into
     * @param pos         The position to drop the contents
     * @param itemHandler The handler to empty
     */
    public static void dropContents(Level level, BlockPos pos, IItemHandler itemHandler) {
        dropContents(level, pos.getCenter(), itemHandler);
    }

    /**
     * Drops the contents of the IItemHandler
     *
     * @param level       The level to drop the contents into
     * @param pos         The position to drop the contents
     * @param itemHandler The handler to empty
     */
    public static void dropContents(Level level, Vec3 pos, IItemHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            int amount = itemHandler.getStackInSlot(slot).getCount();
            while (amount > 0) {
                ItemStack stack = itemHandler.extractItem(slot, amount, false);
                Containers.dropItemStack(level, pos.x, pos.y, pos.z, stack);
                amount = itemHandler.getStackInSlot(slot).getCount();
            }
        }
    }
}
