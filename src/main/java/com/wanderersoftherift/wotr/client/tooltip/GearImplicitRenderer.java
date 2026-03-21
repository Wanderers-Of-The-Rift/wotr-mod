package com.wanderersoftherift.wotr.client.tooltip;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

public record GearImplicitRenderer(GearImplicitsComponent implicitsComponent) implements ClientTooltipComponent {
    public static final Component IMPLICITS_LABEL = Component
            .translatable("tooltip." + WanderersOfTheRift.MODID + ".implicit")
            .withStyle(ChatFormatting.GRAY);
    private static final int IMPLICIT_PADDING = 2;

    @Override
    public int getHeight(Font font) {
        var isKeyDown = ModifierRenderHelper.isKeyDown();
        int tooltipCount = implicitsComponent.implicits.modifierInstances()
                .stream()
                .mapToInt(modifierInstance -> ModifierRenderHelper.countTooltips(modifierInstance, isKeyDown))
                .sum();
        return font.lineHeight * (1 + tooltipCount);
    }

    @Override
    public int getWidth(Font font) {
        var isKeyDown = ModifierRenderHelper.isKeyDown();
        var tooltipMax = implicitsComponent.implicits.modifierInstances()
                .stream()
                .mapToInt(modifierInstance -> ModifierRenderHelper.modifierTextWidth(modifierInstance, isKeyDown, font))
                .reduce(Integer::max)
                .orElse(0);
        return Integer.max(tooltipMax, font.width(IMPLICITS_LABEL));
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        var isKeyDown = ModifierRenderHelper.isKeyDown();
        font.drawInBatch("Implicits: ", (float) x, (float) y, ChatFormatting.GRAY.getColor(), true, matrix,
                bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        y += font.lineHeight + IMPLICIT_PADDING;
        for (var modifier : implicitsComponent.implicits.modifierInstances()) {
            ModifierRenderHelper.renderModifierEffectDescriptions(modifier, isKeyDown, font, x, y, font.lineHeight,
                    matrix, bufferSource);
            y += font.lineHeight * ModifierRenderHelper.countTooltips(modifier, isKeyDown) + IMPLICIT_PADDING;
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        var isKeyDown = ModifierRenderHelper.isKeyDown();
        y += font.lineHeight + IMPLICIT_PADDING;
        for (var modifier : implicitsComponent.implicits.modifierInstances()) {
            ModifierRenderHelper.renderModifierEffectIcons(modifier, isKeyDown, font, x, y, font.lineHeight,
                    guiGraphics);
            y += font.lineHeight * ModifierRenderHelper.countTooltips(modifier, isKeyDown) + IMPLICIT_PADDING;
        }
    }

    public record GearImplicitsComponent(GearImplicits implicits) implements TooltipComponent {
    }
}
