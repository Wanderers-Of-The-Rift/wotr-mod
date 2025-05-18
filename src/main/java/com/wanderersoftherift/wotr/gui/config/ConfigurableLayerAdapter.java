package com.wanderersoftherift.wotr.gui.config;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ConfigurableLayerAdapter is used to provide configuration support to existing, non-configurable layers
 */
public interface ConfigurableLayerAdapter extends ConfigurableLayer {

    /**
     * @return The list of layers this adapter applies to
     */
    List<ResourceLocation> targetLayers();

    /**
     * Called prior to rendering the adapted layer(s) to allow for the pose stack to be altered
     *
     * @param guiGraphics
     */
    void preRender(GuiGraphics guiGraphics);

    /**
     * Called after rendering the adapted layer(s) to allow for the pose stack to be restored
     *
     * @param guiGraphics
     */
    void postRender(GuiGraphics guiGraphics);

    @Override
    default void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
    }
}
