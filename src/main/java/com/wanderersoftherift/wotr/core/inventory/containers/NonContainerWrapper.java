package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

/**
 * Null object container wrapper that contains no items
 */
public final class NonContainerWrapper implements ContainerWrapper {
    public static final ContainerWrapper INSTANCE = new NonContainerWrapper();

    private NonContainerWrapper() {
    }

    @Override
    public void recordChanges() {
    }

    @Override
    public @Nullable ItemAccessor containerItem() {
        return null;
    }

    @Override
    public @NotNull Iterator<ItemAccessor> iterator() {
        return Collections.emptyIterator();
    }
}
