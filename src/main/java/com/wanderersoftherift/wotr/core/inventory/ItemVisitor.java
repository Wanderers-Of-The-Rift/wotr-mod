package com.wanderersoftherift.wotr.core.inventory;

/**
 * Visit for traversing containers
 */
public interface ItemVisitor {

    /**
     * @param item The item being visited. May be empty/air.
     */
    void visit(ItemAccessor item);
}
