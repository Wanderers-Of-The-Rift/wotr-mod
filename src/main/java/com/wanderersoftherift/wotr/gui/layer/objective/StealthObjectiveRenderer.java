package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.rift.objective.AbstractObjective;
import com.wanderersoftherift.wotr.rift.objective.types.StealthObjective;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class StealthObjectiveRenderer extends ObjectiveRenderer {
    private static final ResourceLocation ALARM_SPRITE = WanderersOfTheRift.id("objective/stealth/alarm");
    private static final ResourceLocation ALERT_SPRITE = WanderersOfTheRift.id("objective/stealth/alert");
    private static final ResourceLocation BAR_BACKGROUND_SPRITE = WanderersOfTheRift
            .id("objective/stealth/bar_background");
    private static final ResourceLocation BAR_PROGRESS_SPRITE = WanderersOfTheRift.id("objective/stealth/bar_progress");

    private final StealthObjective stealthObjective;

    public StealthObjectiveRenderer(StealthObjective stealthObjective) {
        this.stealthObjective = stealthObjective;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        int textureWidth = 52;
        int spacing = 5;
        int x = (guiGraphics.guiWidth() / 2) - (textureWidth / 2);
        int y = 14;
        float progress = (float) stealthObjective.getAlarmProgress() / stealthObjective.getMaxProgress();
        int progressWidth = (int) (progress * (textureWidth + 1));
        guiGraphics.blitSprite(RenderType::guiTextured, ALARM_SPRITE, x - spacing - 10, y - 2, 10, 10);
        guiGraphics.blitSprite(RenderType::guiTextured, BAR_BACKGROUND_SPRITE, x, y, textureWidth, 5);
        if (progressWidth > 0) {
            guiGraphics.blitSprite(RenderType::guiTextured, BAR_PROGRESS_SPRITE, textureWidth, 5, 0, 0, x, y,
                    progressWidth, 5);
        }
        if (progress < 0.84F && progress > 0.5F) {
            guiGraphics.blitSprite(RenderType::guiTextured, ALERT_SPRITE, x + textureWidth + spacing, y - 4, 14, 14);
        }
        if (progress >= 0.84F && player.tickCount % 16 < 8) {
            guiGraphics.blitSprite(RenderType::guiTextured, ALERT_SPRITE, x + textureWidth + spacing, y - 5, 14, 14);
        }
    }

    public static StealthObjectiveRenderer create(AbstractObjective objective) {
        if (objective instanceof StealthObjective stealthObjective) {
            return new StealthObjectiveRenderer(stealthObjective);
        }
        return null;
    }
}
