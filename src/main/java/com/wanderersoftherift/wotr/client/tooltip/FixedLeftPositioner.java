package com.wanderersoftherift.wotr.client.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class FixedLeftPositioner implements ClientTooltipPositioner {
    public static final ClientTooltipPositioner INSTANCE = new FixedLeftPositioner();

    private FixedLeftPositioner() {
    }

    public Vector2ic positionTooltip(int screenWidth, int screenHeight, int positionX, int positionY, int tooltipWidth, int tooltipHeight) {
        Vector2i vector2i = (new Vector2i(positionX, positionY)).add(12, -12);
        this.positionTooltip(screenWidth, screenHeight, vector2i, tooltipWidth, tooltipHeight);
        return vector2i;
    }

    private void positionTooltip(int screenWidth, int screenHeight, Vector2i tooltipPos, int tooltipWidth, int tooltipHeight) {
        tooltipPos.x = Math.max(tooltipPos.x - 24 - tooltipWidth, 4);

        int i = tooltipHeight + 3;
        if (tooltipPos.y + i > screenHeight) {
            tooltipPos.y = tooltipPos.y - (tooltipPos.y + i - screenHeight);
        }

    }
}