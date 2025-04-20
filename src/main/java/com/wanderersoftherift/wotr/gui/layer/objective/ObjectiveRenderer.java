package com.wanderersoftherift.wotr.gui.layer.objective;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public abstract class ObjectiveRenderer {
    public static ObjectiveRenderer current = null;

    public abstract void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
