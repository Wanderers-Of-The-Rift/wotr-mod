package com.wanderersoftherift.wotr.gui.menu;

import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class RunegemSlot extends Slot {
    @Nullable
    private GearSocket socket = null;

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

    // prevent bullshit vanilla behavior
    public boolean isHighlightable() {
        return false;
    }
}
