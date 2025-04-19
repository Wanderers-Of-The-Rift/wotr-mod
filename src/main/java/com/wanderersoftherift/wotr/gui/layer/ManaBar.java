package com.wanderersoftherift.wotr.gui.layer;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.init.ModAttachments;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.util.GuiUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2i;

import java.util.List;

/**
 * Displays the player's mana. The bar extends based on max capacity, has an animation to its fill
 */
public class ManaBar implements LayeredDraw.Layer {
    private static final ResourceLocation TEXTURE = WanderersOfTheRift.id("textures/gui/hud/mana_bar.png");
    private static final int TEXTURE_WIDTH = 29;
    private static final int TEXTURE_HEIGHT = 22;

    private static final int MANA_PER_SECTION = 10;
    private static final int SECTION_HEIGHT = 7;

    private static final int BAR_OFFSET_X = 21;
    private static final int BAR_OFFSET_Y = 0;
    private static final int BAR_WIDTH = 5;
    private static final int FILL_WIDTH = 3;
    private static final int TOP_HEIGHT = 1;
    private static final int BOTTOM_HEIGHT = 1;
    private static final int FILL_X_OFFSET = 1;

    private static final int TICKS_PER_FRAME = 6;
    private static final int NUM_FRAMES = 8;
    private static final int FILL_VARIANTS = 3;

    private float animCounter = 0.f;

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) {
            return;
        }
        LocalPlayer player = minecraft.player;
        int maxMana = (int) player.getAttributeValue(ModAttributes.MAX_MANA);
        if (maxMana == 0) {
            return;
        }

        animCounter += deltaTracker.getGameTimeDeltaPartialTick(true);
        while (animCounter > TICKS_PER_FRAME * NUM_FRAMES) {
            animCounter -= TICKS_PER_FRAME * NUM_FRAMES;
        }
        int frame = Mth.floor(animCounter / TICKS_PER_FRAME);

        ManaData mana = player.getData(ModAttachments.MANA);

        int height = renderBar(guiGraphics, mana.getAmount(), maxMana, frame);
        if (!minecraft.mouseHandler.isMouseGrabbed()) {
            Vector2i mousePos = GuiUtil.getMouseScreenPosition();
            renderTooltips(guiGraphics, mana.getAmount(), maxMana, height, mousePos.x, mousePos.y);
        }
    }

    private void renderTooltips(@NotNull GuiGraphics graphics, int amount, int maxMana, int barHeight, int x, int y) {
        if (x < BAR_OFFSET_X || x >= BAR_OFFSET_X + BAR_WIDTH || y < BAR_OFFSET_Y || y >= BAR_OFFSET_Y + barHeight) {
            return;
        }
        graphics.renderComponentTooltip(
                Minecraft.getInstance().font, List.of(Component
                        .translatable(WanderersOfTheRift.translationId("tooltip", "mana_bar"), amount, maxMana)),
                x, y + 8);
    }

    private int renderBar(GuiGraphics graphics, int amount, int maxMana, int frame) {
        float rawSectionCount = (float) maxMana / MANA_PER_SECTION;
        int sectionCount = (int) rawSectionCount;

        int yOffset = BAR_OFFSET_Y;
        int topSectionSize;
        if (maxMana == sectionCount * MANA_PER_SECTION) {
            sectionCount -= 1;
            topSectionSize = SECTION_HEIGHT - 1;
        } else {
            topSectionSize = (int) ((rawSectionCount - Math.floor(rawSectionCount)) * (SECTION_HEIGHT - 1));
        }

        int topSectionFill = topSectionSize * Math.max(0, amount - (sectionCount * MANA_PER_SECTION))
                / (maxMana - sectionCount * MANA_PER_SECTION);
        yOffset += renderTopSection(graphics, frame, sectionCount % FILL_VARIANTS, yOffset, topSectionSize,
                topSectionFill);

        for (int i = 0; i < sectionCount; i++) {
            int fill = SECTION_HEIGHT
                    * Math.clamp(0, MANA_PER_SECTION, amount - (sectionCount - i - 1) * MANA_PER_SECTION)
                    / MANA_PER_SECTION;
            yOffset += renderSection(graphics, frame, (sectionCount - 1 - i) % FILL_VARIANTS, yOffset, fill);
        }
        graphics.blit(RenderType::guiTextured, TEXTURE, BAR_OFFSET_X, yOffset, 0, TEXTURE_HEIGHT - BOTTOM_HEIGHT,
                BAR_WIDTH, BOTTOM_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        return yOffset - BAR_OFFSET_Y;
    }

    private int renderSection(GuiGraphics graphics, int frame, int variant, int yOffset, int fill) {
        graphics.blit(RenderType::guiTextured, TEXTURE, BAR_OFFSET_X, yOffset, 0, SECTION_HEIGHT, BAR_WIDTH,
                SECTION_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (fill > 0) {
            graphics.blit(RenderType::guiTextured, TEXTURE, BAR_OFFSET_X + FILL_X_OFFSET,
                    yOffset + SECTION_HEIGHT - fill, BAR_WIDTH + frame * FILL_WIDTH,
                    SECTION_HEIGHT - fill + SECTION_HEIGHT * variant, FILL_WIDTH, fill, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
        return SECTION_HEIGHT;
    }

    private int renderTopSection(GuiGraphics graphics, int frame, int variant, int yOffset, int size, int fill) {
        graphics.blit(RenderType::guiTextured, TEXTURE, BAR_OFFSET_X, yOffset, 0, 0, BAR_WIDTH, 1 + size, TEXTURE_WIDTH,
                TEXTURE_HEIGHT);
        if (fill > 0) {
            graphics.blit(RenderType::guiTextured, TEXTURE, BAR_OFFSET_X + FILL_X_OFFSET,
                    yOffset + TOP_HEIGHT + size - fill, BAR_WIDTH + frame * FILL_WIDTH,
                    TOP_HEIGHT + size - fill + SECTION_HEIGHT * variant, FILL_WIDTH, fill, TEXTURE_WIDTH,
                    TEXTURE_HEIGHT);
        }
        return size + TOP_HEIGHT;
    }
}
