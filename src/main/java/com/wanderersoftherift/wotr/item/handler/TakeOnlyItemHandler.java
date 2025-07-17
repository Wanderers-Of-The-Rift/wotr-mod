package com.wanderersoftherift.wotr.item.handler;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper around IItemHandler that only allows items to be removed
 */
public class TakeOnlyItemHandler extends BaseItemHandlerWrapper {

    public TakeOnlyItemHandler(IItemHandler parent) {
        super(parent);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return false;
    }
}
