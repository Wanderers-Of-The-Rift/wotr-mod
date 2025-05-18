package com.wanderersoftherift.wotr.gui.config;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

/**
 * Interface for layers that are configurable - they are displayed in the HUD configuration screen and can be moved
 */
public interface ConfigurableLayer extends LayeredDraw.Layer {

    /**
     * @return The name of the Layer (for display in tooltip)
     */
    Component getName();

    /**
     * @return The configuration that controls the layer's positioning
     */
    HudElementConfig getConfig();

    /**
     * @return The width to display the element in the configuration screen. Ideally this is the display width of the
     *         element, but may be representative of its potential width instead. Should not be zero.
     */
    int getConfigWidth();

    /**
     * @return The height to display the element in the configuration screen. Ideally this is the display height of the
     *         element, but may be representative of its potential width instead. Should not be zero.
     */
    int getConfigHeight();

}
