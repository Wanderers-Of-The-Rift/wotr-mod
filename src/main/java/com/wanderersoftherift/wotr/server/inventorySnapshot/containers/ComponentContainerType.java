package com.wanderersoftherift.wotr.server.inventorySnapshot.containers;

import com.google.common.collect.Streams;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * This is the container type for any minecraft item using the CONTAINER data component for item storage (e.g. Shulker
 * Boxes)
 */
public class ComponentContainerType implements ContainerType {
    @Override
    public boolean isContainer(ItemStack item) {
        return item.has(DataComponents.CONTAINER);
    }

    @Override
    public ContainerWrapper getWrapper(ItemStack item) {
        return new ContainerComponentContainerWrapper(item);
    }

    private static class ContainerComponentContainerWrapper implements ContainerWrapper {
        private final ItemStack containerItem;
        private final ItemContainerContents component;

        public ContainerComponentContainerWrapper(ItemStack item) {
            containerItem = item;
            component = containerItem.get(DataComponents.CONTAINER);
        }

        @Override
        public void recordChanges() {
            containerItem.set(DataComponents.CONTAINER, component);
        }

        @Override
        public @NotNull Iterator<ContainerItemWrapper> iterator() {
            return Streams.stream(component.nonEmptyItems())
                    .<ContainerItemWrapper>map(DirectContainerItemWrapper::new)
                    .toList()
                    .iterator();
        }
    }

}
