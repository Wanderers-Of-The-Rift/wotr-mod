package com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot;

import com.google.common.collect.Streams;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

/**
 * This is the container type for any minecraft item using the CONTAINER data component for item storage (e.g. Shulker Boxes)
 */
public class ComponentContainerType implements ContainerType {
    @Override
    public boolean isContainer(ItemStack item) {
        return item.has(DataComponents.CONTAINER);
    }

    @Override
    public Iterable<ContainerItemWrapper> iterateContainerContents(ItemStack item) {
        return Streams.stream(item.get(DataComponents.CONTAINER).nonEmptyItems()).<ContainerItemWrapper>map(DirectContainerItemWrapper::new).toList();
    }
}
