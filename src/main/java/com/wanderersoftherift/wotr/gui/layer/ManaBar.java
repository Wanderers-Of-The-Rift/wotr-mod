package com.wanderersoftherift.wotr.gui.layer;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityResource;
import com.wanderersoftherift.wotr.config.ClientConfig;
import com.wanderersoftherift.wotr.gui.config.ConfigurableLayer;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.gui.config.UIOrientation;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.util.GuiUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Displays the player's mana. The bar extends based on max capacity, has an animation to its fill
 */
public class ManaBar implements ConfigurableLayer {
    private static final Component NAME = Component.translatable(WanderersOfTheRift.translationId("hud", "mana_bar"));
    private static final ResourceLocation TEXTURE_H = WanderersOfTheRift.id("textures/gui/hud/mana_bar_h.png");
    private static final int TEXTURE_H_WIDTH = 22;
    private static final int TEXTURE_H_HEIGHT = 29;

    private static final ResourceLocation TEXTURE_V = WanderersOfTheRift.id("textures/gui/hud/mana_bar_v.png");
    private static final int TEXTURE_V_WIDTH = 29;
    private static final int TEXTURE_V_HEIGHT = 22;

    private static final int MANA_PER_SECTION = 10;
    private static final int START_SECTION_SIZE = 6;
    private static final int SECTION_SIZE = 7;
    private static final int SECTION_OFFSET = 7;

    private static final int BAR_THICKNESS = 5;
    private static final int FILL_THICKNESS = 3;
    private static final int START_SIZE = 1;
    private static final int END_SIZE = 1;
    private static final int FILL_OFFSET = 1;

    private static final int TICKS_PER_FRAME = 6;
    private static final int NUM_FRAMES = 8;
    private static final int FILL_VARIANTS = 3;

    private static final int BAR_SPACING = 8;

    private float animCounter = 0.f;

    private static Set<Map.Entry<Holder<AbilityResource>, Float>> getAbilityResources() {
        return Minecraft.getInstance().player.getData(WotrAttachments.MANA).getAmounts().entrySet();
    }

    @Override
    public Component getName() {
        return NAME;
    }

    @Override
    public HudElementConfig getConfig() {
        return ClientConfig.MANA_BAR;
    }

    @Override
    public int getConfigWidth() {
        var baseWidth = getWidth(100);
        if (getConfig().getOrientation() == UIOrientation.VERTICAL) {
            return baseWidth + getAbilityResources().size() * BAR_SPACING;
        }
        return baseWidth;
    }

    @Override
    public int getConfigHeight() {
        var baseHeight = getHeight(100);
        if (getConfig().getOrientation() == UIOrientation.HORIZONTAL) {
            return baseHeight + getAbilityResources().size() * BAR_SPACING;
        }
        return baseHeight;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        var index = 0;
        for (var resourceEntry : getAbilityResources()) {
            Vector2i offset;

            if (getConfig().getOrientation() == UIOrientation.HORIZONTAL) {
                offset = new Vector2i(0, index * BAR_SPACING);
            } else {
                offset = new Vector2i(-index * BAR_SPACING, 0);
            }

            render(guiGraphics, deltaTracker, resourceEntry.getKey(), resourceEntry.getValue(), offset);
            index++;
        }
    }

    public void render(
            @NotNull GuiGraphics guiGraphics,
            @NotNull DeltaTracker deltaTracker,
            Holder<AbilityResource> resource,
            float amountFloat,
            Vector2i offset) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || !getConfig().isVisible() || minecraft.gameMode == null
                || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = minecraft.player;
        var maxMana = resource.value().maxForEntity(player);
        if (maxMana <= 0) {
            return;
        }

        int width = getWidth((int) maxMana);
        int height = getHeight((int) maxMana);
        Vector2i pos = getConfig().getPosition(width, height, guiGraphics.guiWidth(), guiGraphics.guiHeight())
                .add(offset);

        // todo move with resourceIndex

        animCounter += deltaTracker.getGameTimeDeltaPartialTick(true);
        while (animCounter > TICKS_PER_FRAME * NUM_FRAMES) {
            animCounter -= TICKS_PER_FRAME * NUM_FRAMES;
        }
        int frame = Mth.floor(animCounter / TICKS_PER_FRAME);

        int amount = (int) amountFloat;

        renderBar(guiGraphics, pos, amount, (int) maxMana, frame, resource.value().color());
        renderTooltip(guiGraphics, pos, amount, (int) maxMana, width, height);
    }

    private void renderBar(GuiGraphics graphics, Vector2i pos, int amount, int maxMana, int frame, int color) {
        int sectionCount = maxMana / MANA_PER_SECTION;

        int topSectionSize;
        if (maxMana == sectionCount * MANA_PER_SECTION) {
            sectionCount -= 1;
            topSectionSize = START_SECTION_SIZE;
        } else {
            topSectionSize = START_SECTION_SIZE * (maxMana - sectionCount * MANA_PER_SECTION) / MANA_PER_SECTION;
        }

        if (getConfig().getOrientation() == UIOrientation.HORIZONTAL) {
            renderHBar(graphics, pos, topSectionSize, sectionCount, color);
            renderHFill(graphics, pos, amount, maxMana, topSectionSize, sectionCount, frame, color);
        } else {
            renderVBar(graphics, pos, topSectionSize, sectionCount, color);
            renderVFill(graphics, pos, amount, maxMana, topSectionSize, sectionCount, frame, color);
        }
    }

    private void renderHBar(GuiGraphics graphics, Vector2i pos, int startSectionSize, int sectionCount, int color) {
        int xOffset = pos.x;

        graphics.blit(RenderType::guiTextured, TEXTURE_H, xOffset, pos.y, 0, 0, START_SIZE + startSectionSize,
                BAR_THICKNESS, TEXTURE_H_WIDTH, TEXTURE_H_HEIGHT, color);
        xOffset += START_SIZE + startSectionSize;
        for (int i = 0; i < sectionCount; i++) {
            graphics.blit(RenderType::guiTextured, TEXTURE_H, xOffset, pos.y, SECTION_OFFSET, 0, SECTION_SIZE,
                    BAR_THICKNESS, TEXTURE_H_WIDTH, TEXTURE_H_HEIGHT, color);
            xOffset += SECTION_SIZE;
        }
        graphics.blit(RenderType::guiTextured, TEXTURE_H, xOffset, pos.y, TEXTURE_H_WIDTH - END_SIZE, 0, END_SIZE,
                BAR_THICKNESS, TEXTURE_H_WIDTH, TEXTURE_H_HEIGHT, color);
    }

    private void renderVBar(GuiGraphics graphics, Vector2i pos, int topSectionSize, int sectionCount, int color) {
        int yOffset = pos.y;

        graphics.blit(RenderType::guiTextured, TEXTURE_V, pos.x, yOffset, 0, 0, BAR_THICKNESS,
                START_SIZE + topSectionSize, TEXTURE_V_WIDTH, TEXTURE_V_HEIGHT, color);
        yOffset += START_SIZE + topSectionSize;
        for (int i = 0; i < sectionCount; i++) {
            graphics.blit(RenderType::guiTextured, TEXTURE_V, pos.x, yOffset, 0, SECTION_OFFSET, BAR_THICKNESS,
                    SECTION_SIZE, TEXTURE_V_WIDTH, TEXTURE_V_HEIGHT, color);
            yOffset += SECTION_SIZE;
        }
        graphics.blit(RenderType::guiTextured, TEXTURE_V, pos.x, yOffset, 0, TEXTURE_V_HEIGHT - END_SIZE, BAR_THICKNESS,
                END_SIZE, TEXTURE_V_WIDTH, TEXTURE_V_HEIGHT, color);
    }

    private int calcFillLength(int amount, int min, int max, int sectionSize) {
        int sectionAmount = Math.clamp(0, max - min, amount - min);
        return sectionSize * sectionAmount / (max - min);
    }

    private void renderHFill(
            GuiGraphics graphics,
            Vector2i pos,
            int amount,
            int maxMana,
            int startSectionSize,
            int sectionCount,
            int frame,
            int color) {
        int xOffset = pos.x + START_SIZE;
        int startSectionFill = calcFillLength(amount, sectionCount * MANA_PER_SECTION, maxMana, startSectionSize);
        if (startSectionFill > 0) {
            graphics.blit(RenderType::guiTextured, TEXTURE_H, xOffset + START_SECTION_SIZE - startSectionFill,
                    pos.y + FILL_OFFSET, START_SIZE + START_SECTION_SIZE - startSectionFill,
                    BAR_THICKNESS + frame * FILL_THICKNESS, startSectionFill, FILL_THICKNESS, TEXTURE_H_WIDTH,
                    TEXTURE_H_HEIGHT, color);
        }
        xOffset += START_SECTION_SIZE;
        for (int i = 0; i < sectionCount; i++) {
            int sectionFill = calcFillLength(amount, (sectionCount - i - 1) * MANA_PER_SECTION,
                    (sectionCount - i) * MANA_PER_SECTION, SECTION_SIZE);
            if (sectionFill > 0) {
                graphics.blit(RenderType::guiTextured, TEXTURE_H, xOffset + SECTION_SIZE - sectionFill,
                        pos.y + FILL_OFFSET, SECTION_SIZE - sectionFill + ((i + 1) % FILL_VARIANTS) * SECTION_SIZE,
                        BAR_THICKNESS + frame * FILL_THICKNESS, sectionFill, FILL_THICKNESS, TEXTURE_H_WIDTH,
                        TEXTURE_H_HEIGHT, color);
            }
            xOffset += SECTION_SIZE;
        }
    }

    private void renderVFill(
            GuiGraphics graphics,
            Vector2i pos,
            int amount,
            int maxMana,
            int startSectionSize,
            int sectionCount,
            int frame,
            int color) {
        int yOffset = pos.y + START_SIZE;
        int startSectionFill = calcFillLength(amount, sectionCount * MANA_PER_SECTION, maxMana, startSectionSize);
        if (startSectionFill > 0) {
            graphics.blit(RenderType::guiTextured, TEXTURE_V, pos.x + FILL_OFFSET,
                    yOffset + START_SECTION_SIZE - startSectionFill, BAR_THICKNESS + frame * FILL_THICKNESS,
                    START_SIZE + START_SECTION_SIZE - startSectionFill, FILL_THICKNESS, startSectionFill,
                    TEXTURE_V_WIDTH, TEXTURE_V_HEIGHT, color);
        }
        yOffset += START_SECTION_SIZE;
        for (int i = 0; i < sectionCount; i++) {
            int sectionFill = calcFillLength(amount, (sectionCount - i - 1) * MANA_PER_SECTION,
                    (sectionCount - i) * MANA_PER_SECTION, SECTION_SIZE);
            if (sectionFill > 0) {
                graphics.blit(RenderType::guiTextured, TEXTURE_V, pos.x + FILL_OFFSET,
                        yOffset + SECTION_SIZE - sectionFill, BAR_THICKNESS + frame * FILL_THICKNESS,
                        SECTION_SIZE - sectionFill + ((i + 1) % FILL_VARIANTS) * SECTION_SIZE, FILL_THICKNESS,
                        sectionFill, TEXTURE_V_WIDTH, TEXTURE_V_HEIGHT, color);
            }
            yOffset += SECTION_SIZE;
        }
    }

    private void renderTooltip(
            @NotNull GuiGraphics graphics,
            Vector2i pos,
            int amount,
            int maxMana,
            int width,
            int height) {
        if (!(Minecraft.getInstance().screen instanceof ChatScreen)) {
            return;
        }
        Vector2i mouse = GuiUtil.getMouseScreenPosition();
        if (mouse.x < pos.x || mouse.x >= pos.x + width || mouse.y < pos.y || mouse.y >= pos.y + height) {
            return;
        }
        graphics.renderComponentTooltip(
                Minecraft.getInstance().font, List.of(Component
                        .translatable(WanderersOfTheRift.translationId("tooltip", "mana_bar"), amount, maxMana)),
                mouse.x, mouse.y + 8);
    }

    private int getWidth(int maxMana) {
        if (getConfig().getOrientation() == UIOrientation.VERTICAL) {
            return BAR_THICKNESS;
        } else {
            return length(maxMana);
        }
    }

    private int getHeight(int maxMana) {
        if (getConfig().getOrientation() == UIOrientation.VERTICAL) {
            return length(maxMana);
        } else {
            return BAR_THICKNESS;
        }
    }

    private int length(int maxMana) {
        int sectionCount = java.lang.Math.ceilDiv(maxMana, MANA_PER_SECTION) - 1;
        int topSectionSize = START_SECTION_SIZE * (maxMana - sectionCount * MANA_PER_SECTION) / MANA_PER_SECTION;

        return topSectionSize + sectionCount * SECTION_SIZE + END_SIZE + START_SIZE;
    }

}
