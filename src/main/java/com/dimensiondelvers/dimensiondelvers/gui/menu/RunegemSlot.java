package com.dimensiondelvers.dimensiondelvers.gui.menu;

import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemShape;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class RunegemSlot extends Slot {
    @Nullable
    private RuneGemShape shape = null;
    public boolean isDirty = false;
    private int socketIndex = 0;

    public RunegemSlot(Container container, int slot, int x, int y, @Nullable RuneGemShape shape) {
        super(container, slot, x, y);
        this.shape = shape;
        socketIndex = slot;
    }

    public RuneGemShape getShape() {
        return shape;
    }

    public void setShape(RuneGemShape shape) {
        this.shape = shape;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public int getSocketIndex() {
        return socketIndex;
    }
}
