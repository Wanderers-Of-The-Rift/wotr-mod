package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.StealthOngoingObjective;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

public class StealthObjectiveRenderer extends ObjectiveRenderer {
    private static final ResourceLocation ALARM_SPRITE = WanderersOfTheRift.id("objective/stealth/alarm");
    private static final ResourceLocation ALERT_SPRITE = WanderersOfTheRift.id("objective/stealth/alert");
    private static final ResourceLocation BAR_BACKGROUND_SPRITE = WanderersOfTheRift
            .id("objective/stealth/bar_background");
    private static final ResourceLocation BAR_PROGRESS_SPRITE = WanderersOfTheRift.id("objective/stealth/bar_progress");

    private final StealthOngoingObjective stealthObjective;

    public StealthObjectiveRenderer(StealthOngoingObjective stealthObjective) {
        this.stealthObjective = stealthObjective;
    }

    @Override
    public void render(GuiGraphics guiGraphics, HudElementConfig config, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        int textureWidth = 52;
        int spacing = 5;
        int alarmWidth = 10;
        Vector2i pos = config.getPosition(textureWidth + 2 * (spacing + alarmWidth), 10, guiGraphics.guiWidth(),
                guiGraphics.guiHeight());

        float progress = (float) stealthObjective.getAlarmProgress() / stealthObjective.getTargetProgress();
        int progressWidth = (int) (progress * (textureWidth + 1));
        guiGraphics.blitSprite(RenderType::guiTextured, ALARM_SPRITE, pos.x, pos.y, 10, 10);
        pos.x += alarmWidth + spacing;
        pos.y += 2;
        guiGraphics.blitSprite(RenderType::guiTextured, BAR_BACKGROUND_SPRITE, pos.x, pos.y, textureWidth, 5);
        if (progressWidth > 0) {
            guiGraphics.blitSprite(RenderType::guiTextured, BAR_PROGRESS_SPRITE, textureWidth, 5, 0, 0, pos.x, pos.y,
                    progressWidth, 5);
        }
        if (progress < 0.84F && progress > 0.5F) {
            guiGraphics.blitSprite(RenderType::guiTextured, ALERT_SPRITE, pos.x + textureWidth + spacing, pos.y - 4, 14,
                    14);
        }
        if (progress >= 0.84F && player.tickCount % 16 < 8) {
            guiGraphics.blitSprite(RenderType::guiTextured, ALERT_SPRITE, pos.x + textureWidth + spacing, pos.y - 5, 14,
                    14);
        }
    }

    public static StealthObjectiveRenderer create(OngoingObjective objective) {
        if (objective instanceof StealthOngoingObjective stealthObjective) {
            return new StealthObjectiveRenderer(stealthObjective);
        }
        return null;
    }
}
