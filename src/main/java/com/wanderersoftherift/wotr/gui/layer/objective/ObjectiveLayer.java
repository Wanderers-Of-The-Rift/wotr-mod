package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ObjectiveLayer implements ConfigurableLayer {

    private static final Component NAME = Component.translatable(WanderersOfTheRift.translationId("hud", "objective"));

    private static ObjectiveRenderer current = null;

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || !getConfig().isVisible() || current == null) {
            return;
        }

        current.render(guiGraphics, getConfig(), deltaTracker);
    }

    public static ObjectiveRenderer getRenderer() {
        return current;
    }

    public static void setRenderer(ObjectiveRenderer renderer) {
        current = renderer;
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
