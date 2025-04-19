package com.wanderersoftherift.wotr.server.inventorySnapshot.containers;

/**
 * Wrapper for a container. Provides iteration over its contents and any direct methods. For some containers, changes
 * must be recorded after being made - largely those that use data components, as the components themselves are (or
 * should be treated as) immutable
 */
public interface ContainerWrapper extends Iterable<ContainerItemWrapper> {

    /**
     * If necessary, saves any changes back to the container. Some container implementation may save changes directly so
     * this will be noop
     */
    void recordChanges();
}
