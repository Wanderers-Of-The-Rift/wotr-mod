package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import net.minecraft.world.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class VanillaContainerWrapper implements ContainerWrapper {
    private final Container container;

    public VanillaContainerWrapper(Container container) {
        this.container = container;
    }

    @Override
    public void recordChanges() {
        // NOOP
    }

    @Override
    public @Nullable ItemAccessor containerItem() {
        return null;
    }

    @Override
    public @NotNull Iterator<ItemAccessor> iterator() {
        return new VanillaContainerIterator(container);
    }

    private static class VanillaContainerIterator implements Iterator<ItemAccessor> {
        private final Container container;
        private final int size;
        private int slot = 0;

        public VanillaContainerIterator(Container container) {
            this.container = container;
            this.size = container.getContainerSize();
        }

        @Override
        public boolean hasNext() {
            return slot < size;
        }

        @Override
        public ItemAccessor next() {
            return new ContainerItemAccessor(container, slot++);
        }
    }
}
