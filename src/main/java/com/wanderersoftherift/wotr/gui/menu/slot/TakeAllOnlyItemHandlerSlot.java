package com.wanderersoftherift.wotr.gui.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Slot for an ItemHandler that only allows items to be removed
 */
public class TakeAllOnlyItemHandlerSlot extends SlotItemHandler {
    public TakeAllOnlyItemHandlerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        if (amount != getItem().getCount()) {
            return ItemStack.EMPTY;
        }
        return super.remove(amount);
    }

    @Override
    public @NotNull Optional<ItemStack> tryRemove(int count, int decrement, @NotNull Player player) {
        if (count != getItem().getCount()) {
            return Optional.empty();
        }
        return super.tryRemove(count, decrement, player);
    }
}
