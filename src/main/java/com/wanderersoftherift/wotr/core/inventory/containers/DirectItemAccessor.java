package com.wanderersoftherift.wotr.core.inventory.containers;

import com.wanderersoftherift.wotr.core.inventory.ItemAccessor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper for directly working with an item stack for situations where that is appropriate.
 */
public class DirectItemAccessor implements ItemAccessor {

    private final ItemStack item;
    private boolean modified;

    public DirectItemAccessor(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getReadOnlyItemStack() {
        return item;
    }

    @Override
    public List<ItemStack> split(int amount) {
        modified = true;
        return Collections.singletonList(item.split(amount));
    }

    @Override
    public List<ItemStack> remove() {
        modified = true;
        return Collections.singletonList(item.copyAndClear());
    }

    @Override
    public void replace(ItemStack stack) {
        throw new UnsupportedOperationException("Cannot replaces a non-contained item");
    }

    @Override
    public void applyComponents(DataComponentPatch patch) {
        modified = true;
        item.applyComponents(patch);
    }

    @Override
    public boolean isModified() {
        return false;
    }

}
