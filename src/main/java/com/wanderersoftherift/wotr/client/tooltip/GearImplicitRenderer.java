package com.wanderersoftherift.wotr.client.tooltip;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.joml.Matrix4f;

public record GearImplicitRenderer(GearImplicitsComponent implicitsComponent) implements ClientTooltipComponent {

    public static final Component IMPLICITS_LABEL = Component
            .translatable("tooltip." + WanderersOfTheRift.MODID + ".implicit")
            .withStyle(ChatFormatting.GRAY);

    @Override
    public int getHeight(Font font) {
        var isKeyDown = ModifierRenderHelper.isKeyDown();
        int tooltipCount = implicitsComponent.implicits.modifierInstances()
                .stream()
                .mapToInt(modifierInstance -> ModifierRenderHelper.countTooltips(modifierInstance, isKeyDown))
                .sum();
        return 12 * (1 + tooltipCount);
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
        int x2 = x;
        int y2 = y;
        for (var modifier : implicitsComponent.implicits.modifierInstances()) {
            ModifierRenderHelper.renderModifierEffectDescriptions(modifier, isKeyDown, font, x2, y2, 12, matrix,
                    bufferSource);
            y += 12 * ModifierRenderHelper.countTooltips(modifier, isKeyDown);
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        var isKeyDown = ModifierRenderHelper.isKeyDown();
        int x2 = x;
        int y2 = y;
        for (var modifier : implicitsComponent.implicits.modifierInstances()) {
            ModifierRenderHelper.renderModifierEffectIcons(modifier, isKeyDown, font, x2, y2, 12, guiGraphics);
            y += 12 * ModifierRenderHelper.countTooltips(modifier, isKeyDown);
        }
    }

    public record GearImplicitsComponent(GearImplicits implicits) implements TooltipComponent {
    }
}
