package com.wanderersoftherift.wotr.core.inventory.containers;

import com.google.common.collect.Streams;
import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        private final ItemAccessor containerItem;
        private final ItemContainerContents component;

        public ContainerComponentContainerWrapper(ItemStack item) {
            this(new DirectItemAccessor(item));
        }

        public ContainerComponentContainerWrapper(ItemAccessor item) {
            containerItem = item;
            component = item.getReadOnlyItemStack().get(DataComponents.CONTAINER);
        }

        @Override
        public @Nullable ItemAccessor containerItem() {
            return containerItem;
        }

        @Override
        public void recordChanges() {
            containerItem
                    .applyComponents(DataComponentPatch.builder().set(DataComponents.CONTAINER, component).build());
        }

        @Override
        public @NotNull Iterator<ItemAccessor> iterator() {
            return Streams.stream(component.nonEmptyItems())
                    .<ItemAccessor>map(DirectItemAccessor::new)
                    .toList()
                    .iterator();
        }
    }

}
