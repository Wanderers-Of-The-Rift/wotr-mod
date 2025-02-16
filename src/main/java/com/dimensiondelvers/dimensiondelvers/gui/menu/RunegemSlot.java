package com.dimensiondelvers.dimensiondelvers.gui.menu;

import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.dimensiondelvers.dimensiondelvers.item.socket.GearSocket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunegemSlot extends Slot {

    @Nullable
    private GearSocket socket = null;

    private boolean mayTake = true;

    public RunegemSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    public @Nullable RunegemShape getShape() {
        return socket != null ? socket.shape() : null;
    }

    public @Nullable GearSocket getSocket() {
        return socket;
    }

    public void setSocket(@Nullable GearSocket socket) {
        this.socket = socket;
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
