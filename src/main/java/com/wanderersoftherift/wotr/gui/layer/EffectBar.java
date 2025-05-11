package com.wanderersoftherift.wotr.gui.layer;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectDisplayData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectMarker;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.gui.config.UIOrientation;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.util.GuiUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.List;

/**
 * Displays effect markers attached to the player
 */
public final class EffectBar implements ConfigurableLayer {
    private static final Component NAME = Component.translatable(WanderersOfTheRift.translationId("hud", "effect_bar"));

    private static final float FAST_PULSE_THRESHOLD = 40;
    private static final float FAST_PULSE_ON = 0.4f;
    private static final float FAST_PULSE_OFF = 0.2f;
    private static final float SLOW_PULSE_THRESHOLD = 100;
    private static final float SLOW_PULSE_ON = 0.8f;
    private static final float SLOW_PULSE_OFF = 0.2f;
    private static final int ICON_SIZE = 16;

    private float time;

    @Override
    public Component getName() {
        return NAME;
    }

    @Override
    public HudElementConfig getConfig() {
        return ClientConfig.EFFECT_DISPLAY;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !getConfig().isVisible()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        EffectDisplayData data = player.getData(ModAttachments.EFFECT_DISPLAY);
        if (data.size() == 0) {
            return;
        }

        time = (time + 0.05f * deltaTracker.getGameTimeDeltaPartialTick(true)) % 1.0f;
        Vector2i pos = getPosition(data.size(), graphics.guiWidth(), graphics.guiHeight());
        Vector2i dir = getConfig().getOrientation().axis().mul(ICON_SIZE, new Vector2i());

        renderEffects(graphics, pos, dir, data);
        if (minecraft.screen instanceof ChatScreen) {
            Vector2i mousePos = GuiUtil.getMouseScreenPosition();
            renderTooltips(graphics, pos, dir, data, mousePos.x, mousePos.y);
        }
    }

    private void renderTooltips(
            @NotNull GuiGraphics graphics,
            Vector2ic pos,
            Vector2ic dir,
            EffectDisplayData data,
            int x,
            int y) {
        Vector2i end = dir.mul(data.size() - 1, new Vector2i()).add(pos).add(ICON_SIZE, ICON_SIZE);
        if (x < pos.x() || x >= end.x() || y < pos.y() || y >= end.y()) {
            return;
        }

        int index;
        if (dir.x() != 0) {
            index = (x - pos.x()) / ICON_SIZE;
        } else {
            index = (y - pos.y()) / ICON_SIZE;
        }
        var iterator = data.iterate();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        var entry = iterator.next();
        graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(entry.getKey().value().getLabel()), x,
                y + 8);
    }

    private void renderEffects(GuiGraphics graphics, Vector2ic start, Vector2ic dir, EffectDisplayData data) {
        Vector2i pos = new Vector2i(start);

        ObjectIterator<Object2IntMap.Entry<Holder<EffectMarker>>> iterator = data.iterate();
        while (iterator.hasNext()) {
            Object2IntMap.Entry<Holder<EffectMarker>> entry = iterator.next();
            EffectMarker marker = entry.getKey().value();
            boolean show = true;
            if (entry.getIntValue() < FAST_PULSE_THRESHOLD) {
                show = time % (FAST_PULSE_OFF + FAST_PULSE_ON) > FAST_PULSE_OFF;
            } else if (entry.getIntValue() < SLOW_PULSE_THRESHOLD) {
                show = time % (SLOW_PULSE_OFF + SLOW_PULSE_ON) > SLOW_PULSE_OFF;
            }
            if (show) {
                graphics.blit(RenderType::guiTextured, marker.icon(), pos.x, pos.y, 0, 0, ICON_SIZE, ICON_SIZE,
                        ICON_SIZE, ICON_SIZE);
            }

            pos.add(dir);
        }
    }

    private Vector2i getPosition(int effects, int screenWidth, int screenHeight) {
        return ClientConfig.EFFECT_DISPLAY.getPosition(ICON_SIZE, ICON_SIZE * effects, screenWidth, screenHeight);
    }

    @Override
    public int getConfigWidth() {
        if (getConfig().getOrientation() == UIOrientation.HORIZONTAL) {
            return ICON_SIZE * 5;
        } else {
            return ICON_SIZE;
        }
    }

    @Override
    public int getConfigHeight() {
        if (getConfig().getOrientation() == UIOrientation.HORIZONTAL) {
            return ICON_SIZE;
        } else {
            return ICON_SIZE * 5;
        }
    }
}
