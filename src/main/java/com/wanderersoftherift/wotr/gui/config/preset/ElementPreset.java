package com.wanderersoftherift.wotr.gui.config.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.gui.config.ScreenAnchor;
import com.wanderersoftherift.wotr.gui.config.UIOrientation;

import java.util.Optional;

/**
 * An ElementPreset provides configuration for a specific HUD Element (or layer)
 * 
 * @param visible     Whether the element is visible
 * @param anchor      The screen position to place the element relative to
 * @param xOffset     The horizontal offset for placing the element
 * @param yOffset     The vertical offset for placing the element
 * @param orientation The orientation of the element, if rotatable
 */
public record ElementPreset(boolean visible, ScreenAnchor anchor, int xOffset, int yOffset,
        Optional<UIOrientation> orientation) {

    public static final Codec<ElementPreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("visible").forGetter(ElementPreset::visible),
            ScreenAnchor.CODEC.fieldOf("anchor").forGetter(ElementPreset::anchor),
            Codec.INT.fieldOf("xOffset").forGetter(ElementPreset::xOffset),
            Codec.INT.fieldOf("yOffset").forGetter(ElementPreset::yOffset),
            UIOrientation.CODEC.optionalFieldOf("orientation").forGetter(ElementPreset::orientation)
    ).apply(instance, ElementPreset::new)
    );

    /**
     * Applies the preset to the provided element config
     * 
     * @param config
     */
    public void applyTo(HudElementConfig config) {
        config.setVisible(visible());
        config.setX(xOffset());
        config.setY(yOffset());
        config.setAnchor(anchor());
        if (config.hasOrientation() && orientation().isPresent()) {
            config.setOrientation(orientation().get());
        }
    }
}
