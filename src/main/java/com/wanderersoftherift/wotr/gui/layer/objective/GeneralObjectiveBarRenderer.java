package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ProgressObjective;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

public class GeneralObjectiveBarRenderer extends ObjectiveRenderer {
    private static final ResourceLocation BAR_BACKGROUND_SPRITE = WanderersOfTheRift
            .id("objective/general/bar_background");
    private static final ResourceLocation BAR_PROGRESS_SPRITE = WanderersOfTheRift.id("objective/general/bar_progress");
    private static final int BAR_WIDTH = 102;
    private static final int BAR_HEIGHT = 5;

    private static final Component OBJECTIVE_COMPLETE = Component
            .translatable(WanderersOfTheRift.translationId("gui", "objective_status.complete"));

    private final ProgressObjective objective;

    public GeneralObjectiveBarRenderer(ProgressObjective objective) {
        this.objective = objective;
    }

    @Override
    public void render(GuiGraphics guiGraphics, HudElementConfig config, DeltaTracker deltaTracker) {
        if (objective.isComplete()) {
            Font font = Minecraft.getInstance().font;
            int width = font.width(OBJECTIVE_COMPLETE);
            Vector2i pos = config.getPosition(width, font.lineHeight, guiGraphics.guiWidth(), guiGraphics.guiHeight());
            guiGraphics.drawString(font, OBJECTIVE_COMPLETE, pos.x, pos.y, ChatFormatting.WHITE.getColor());
        } else {
            Vector2i pos = config.getPosition(BAR_WIDTH, BAR_HEIGHT, guiGraphics.guiWidth(), guiGraphics.guiHeight());
            float progress = (float) objective.getCurrentProgress() / objective.getTargetProgress();
            int progressWidth = (int) (progress * (BAR_WIDTH));
            guiGraphics.blitSprite(RenderType::guiTextured, BAR_BACKGROUND_SPRITE, pos.x, pos.y, BAR_WIDTH, BAR_HEIGHT);
            if (progressWidth > 0) {
                guiGraphics.blitSprite(RenderType::guiTextured, BAR_PROGRESS_SPRITE, BAR_WIDTH, BAR_HEIGHT, 0, 0, pos.x,
                        pos.y, progressWidth, BAR_HEIGHT);
            }
        }
    }

    public static GeneralObjectiveBarRenderer create(OngoingObjective objective) {
        if (objective instanceof ProgressObjective progressObjective) {
            return new GeneralObjectiveBarRenderer(progressObjective);
        }
        return null;
    }
}
