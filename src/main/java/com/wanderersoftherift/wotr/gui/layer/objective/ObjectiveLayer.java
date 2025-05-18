package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ObjectiveLayer implements ConfigurableLayer {

    private static final Component NAME = Component.translatable(WanderersOfTheRift.translationId("hud", "objective"));

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || !getConfig().isVisible() || ObjectiveRenderer.current == null) {
            return;
        }
        ObjectiveRenderer.current.render(guiGraphics, getConfig(), deltaTracker);
    }

    @Override
    public Component getName() {
        return NAME;
    }

    @Override
    public HudElementConfig getConfig() {
        return ClientConfig.OBJECTIVE;
    }

    @Override
    public int getConfigWidth() {
        return 102;
    }

    @Override
    public int getConfigHeight() {
        return 24;
    }
}
