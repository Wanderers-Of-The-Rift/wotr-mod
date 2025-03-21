package com.wanderersoftherift.wotr.gui.layer.objective;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class ObjectiveLayer implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(ObjectiveRenderer.CURRENT != null) {
            ObjectiveRenderer.CURRENT.render(guiGraphics, deltaTracker);
        }
    }
}
