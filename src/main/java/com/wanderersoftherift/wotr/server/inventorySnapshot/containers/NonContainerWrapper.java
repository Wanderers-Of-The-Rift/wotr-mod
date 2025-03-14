package com.wanderersoftherift.wotr.server.inventorySnapshot.containers;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

/**
 * Null object container wrapper that contains no items
 */
public final class NonContainerWrapper implements ContainerWrapper {
    public static final ContainerWrapper INSTANCE = new NonContainerWrapper();

    private NonContainerWrapper() {}

    @Override
    public void recordChanges() {
    }

    @Override
    public @NotNull Iterator<ContainerItemWrapper> iterator() {
        return Collections.emptyIterator();
    }
}
