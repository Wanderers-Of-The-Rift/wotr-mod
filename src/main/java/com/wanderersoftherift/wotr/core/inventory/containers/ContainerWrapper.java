package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for a container. Provides iteration over its contents and any direct methods. For some containers, changes
 * must be recorded after being made - largely those that use data components, as the components themselves are (or
 * should be treated as) immutable
 */
public interface ContainerWrapper extends Iterable<ItemAccessor> {

    /**
     * If necessary, saves any changes back to the container. Some container implementation may save changes directly so
     * this will be noop
     */
    void recordChanges();

    /**
     * @return The container item, if any. Will be modified by recordChanges
     */
    @Nullable ItemAccessor containerItem();
}
