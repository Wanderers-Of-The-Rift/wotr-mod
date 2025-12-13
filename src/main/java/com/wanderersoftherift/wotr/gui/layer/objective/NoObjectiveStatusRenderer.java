package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.NoOngoingObjective;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

/**
 * Renderer for the no objective.
 */
public class NoObjectiveStatusRenderer extends ObjectiveRenderer {

    private static final Component MESSAGE = Component
            .translatable(WanderersOfTheRift.translationId("objective", "nothing.message"));

    public NoObjectiveStatusRenderer() {
    }

    public static NoObjectiveStatusRenderer create(OngoingObjective objective) {
        if (objective instanceof NoOngoingObjective) {
            return new NoObjectiveStatusRenderer();
        }
        return null;
    }

    @Override
    public void render(GuiGraphics guiGraphics, HudElementConfig config, DeltaTracker deltaTracker) {
        Font font = Minecraft.getInstance().font;
        int width = font.width(MESSAGE);
        Vector2i pos = config.getPosition(width, font.lineHeight, guiGraphics.guiWidth(), guiGraphics.guiHeight());
        guiGraphics.drawString(font, MESSAGE, pos.x, pos.y, ChatFormatting.WHITE.getColor(), true);
    }
}
