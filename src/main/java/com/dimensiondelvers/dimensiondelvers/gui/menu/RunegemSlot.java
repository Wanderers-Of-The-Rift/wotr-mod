package com.dimensiondelvers.dimensiondelvers.gui.menu;

import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunegemSlot extends Slot {
    @Nullable
    private RunegemShape shape = null;

    private boolean mayTake = true;

    public RunegemSlot(Container container, int slot, int x, int y, @Nullable RunegemShape shape) {
        super(container, slot, x, y);
        this.shape = shape;
    }

    public @Nullable RunegemShape getShape() {
        return shape;
    }

    public void setShape(@Nullable RunegemShape shape) {
        this.shape = shape;
    }

    public boolean isDisabled() {
        return false;
    }

    public boolean getMayTake() {
        return mayTake;
    }

    public void setMayTake(boolean mayTake) {
        this.mayTake = mayTake;
    }

    public boolean mayPickup(@NotNull Player player) {
        return mayTake;
    }

    // prevent bullshit vanilla behavior
    public boolean isHighlightable() {
        return false;
    }
}
