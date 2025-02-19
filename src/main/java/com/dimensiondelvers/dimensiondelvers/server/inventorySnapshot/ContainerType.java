package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import net.minecraft.world.item.ItemStack;

/**
 * A container type provides the ability to identify and work with containers of that type, for the Inventory Snapshot system
 */
public interface ContainerType {

    /**
     * @return If the provided item is a container type handled by this ContainerType
     */
    boolean isContainer(ItemStack item);

    /**
     * @return Iterator over the non-empty contents of the given container
     */
    Iterable<ContainerItemWrapper> iterateContainerContents(ItemStack item);

}
