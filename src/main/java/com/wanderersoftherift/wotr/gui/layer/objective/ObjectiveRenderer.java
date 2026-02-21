package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface ObjectiveRenderer {
    void render(GuiGraphics guiGraphics, HudElementConfig config, DeltaTracker deltaTracker);
}
