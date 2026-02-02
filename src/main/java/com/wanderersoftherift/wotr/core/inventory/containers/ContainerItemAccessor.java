package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * For accessing items within a vanilla minecraft Container
 */
public final class ContainerItemAccessor implements ItemAccessor {
    private final Container container;
    private final int index;
    private boolean modified = false;

    public ContainerItemAccessor(Container container, int index) {
        this.container = container;
        this.index = index;
    }

    @Override
    public ItemStack getReadOnlyItemStack() {
        return container.getItem(index);
    }

    @Override
    public List<ItemStack> split(int amount) {
        modified = true;
        return List.of(container.removeItem(index, amount));
    }

    @Override
    public List<ItemStack> remove() {
        modified = true;
        return List.of(container.removeItemNoUpdate(index));
    }

    @Override
    public void replace(ItemStack stack) {
        modified = true;
        container.setItem(index, stack);
    }

    @Override
    public void applyComponents(DataComponentPatch patch) {
        modified = true;
        container.getItem(index).applyComponents(patch);
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public Container container() {
        return container;
    }

    public int index() {
        return index;
    }

}
