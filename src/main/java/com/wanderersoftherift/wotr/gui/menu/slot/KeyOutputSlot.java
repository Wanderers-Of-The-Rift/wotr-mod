package com.wanderersoftherift.wotr.gui.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This slot prevents items being placed into it, and manages the removal of input items when the output is taken
 */
public class KeyOutputSlot extends Slot {

    private final Container inputContainer;

    public KeyOutputSlot(Container container, int slot, int x, int y, Container inputs) {
        super(container, slot, x, y);
        inputContainer = inputs;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
        inputContainer.clearContent();
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        inputContainer.clearContent();
    }

    @Override
    public boolean isFake() {
        return true;
    }
}
