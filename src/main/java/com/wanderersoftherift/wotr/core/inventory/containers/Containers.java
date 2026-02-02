package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import com.wanderersoftherift.wotr.core.inventory.ItemVisitor;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Helper methods for working with containers
 */
public final class Containers {

    private Containers() {
    }

    public static boolean isContainer(Registry<ContainerType> containerTypes, ItemStack item) {
        return containerTypes.stream().anyMatch(type -> type.isContainer(item));
    }

    /**
     * Visits all items in a player's inventory, recursing through containers
     *
     * @param player
     * @param visitor
     */
    public static void walk(Player player, ItemVisitor visitor) {
        Registry<ContainerType> containerTypes = player.level()
                .registryAccess()
                .lookupOrThrow(WotrRegistries.Keys.CONTAINER_TYPES);
        walk(containerTypes, new VanillaContainerWrapper(player.getInventory()), visitor);
    }

    /**
     * Visits all items in a container, recursing through containers
     *
     * @param containerTypes The container type registry
     * @param container      The container to visit
     * @param visitor        The visitor that will visit each item
     */
    public static void walk(Registry<ContainerType> containerTypes, ContainerWrapper container, ItemVisitor visitor) {
        boolean modified = false;
        for (ItemAccessor item : container) {
            visitor.visit(item);
            ContainerWrapper contents = getContents(containerTypes, item.getReadOnlyItemStack());
            if (contents != null) {
                walk(containerTypes, contents, visitor);
            }
            modified |= item.isModified();
        }
        if (modified) {
            container.recordChanges();
        }
    }

    /**
     * Gets the contents of an item, if it is a container
     * 
     * @param containerTypes
     * @param stack
     * @return A ContainerWrapper, or null if the stack is not a (supported) container
     */
    public static @Nullable ContainerWrapper getContents(Registry<ContainerType> containerTypes, ItemStack stack) {
        return containerTypes.stream()
                .filter(type -> type.isContainer(stack))
                .findAny()
                .map(type -> type.getWrapper(stack))
                .orElse(null);
    }
}
