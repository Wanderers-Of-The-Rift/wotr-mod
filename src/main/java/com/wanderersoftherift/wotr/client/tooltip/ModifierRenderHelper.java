package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.util.ComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

public class ModifierRenderHelper {
    public static int modifierTextWidth(ModifierInstance modifierInstance, boolean isShiftDown, Font font) {
        var maxWidth = 0;

        int tier = modifierInstance.tier();
        var modifierTier = modifierInstance.modifier().value().getModifierTier(tier);
        for (AbstractModifierEffect effect : modifierTier) {
            var tooltips = getTooltipsForEffect(effect, isShiftDown, modifierInstance);
            for (var tooltip : tooltips) {
                String text = getEffectText(tooltip);
                maxWidth = Math.max(maxWidth, font.width("> " + text) + 30);
            }
        }
        return maxWidth;
    }

    public static List<ImageComponent> getTooltipsForEffect(
            AbstractModifierEffect effect,
            boolean isKeyDown,
            ModifierInstance modifier) {
        if (!isKeyDown) {
            return effect.getTooltipComponent(ItemStack.EMPTY, modifier.roll(), modifier.modifier().value().getStyle());
        } else {
            return effect.getAdvancedTooltipComponent(ItemStack.EMPTY, modifier.roll(),
                    modifier.modifier().value().getStyle(), modifier.tier());
        }
    }

    private static String getEffectText(TooltipComponent tooltip) {
        if (tooltip instanceof ImageComponent img) {
            return img.base().getString();
        } else {
            return "";
        }
    }

    public static void renderModifierEffectDescriptions(
            ModifierInstance modifierInstance,
            boolean isKeyDown,
            Font font,
            int x,
            int y,
            int lineHeight,
            Matrix4f transform,
            MultiBufferSource.BufferSource buffer) {
        var effects = modifierInstance.effects();
        var i2 = 0;
        for (var effect : effects) {
            var tooltips = getTooltipsForEffect(effect, isKeyDown, modifierInstance);
            for (var tooltip : tooltips) {
                if (tooltip instanceof ImageComponent img) {
                    Function<Component, Component> textTransform;
                    if (modifierInstance.tier() == modifierInstance.modifier().value().getModifierTierList().size()) {
                        textTransform = it -> ComponentUtil.wavingComponent(img.base(),
                                modifierInstance.modifier().value().getStyle().getColor().getValue(), 0.125f, 0.5f);
                    } else {
                        textTransform = it -> it.copy().withStyle(modifierInstance.modifier().value().getStyle());
                    }
                    ImageTooltipRenderer.renderText(img, font, x, y + (i2++) * lineHeight, transform, buffer,
                            textTransform);
                }
            }
        }
    }

    public static void renderModifierEffectIcons(
            ModifierInstance modifierInstance,
            boolean isKeyDown,
            Font font,
            int x,
            int y,
            int lineHeight,
            @NotNull GuiGraphics guiGraphics) {
        var effects = modifierInstance.effects();
        var i2 = 0;
        for (var effect : effects) {
            var tooltips = getTooltipsForEffect(effect, isKeyDown, modifierInstance);
            for (var tooltip : tooltips) {
                if (tooltip instanceof ImageComponent img) {
                    ImageTooltipRenderer.renderImage(img, x, y + (i2++) * lineHeight, guiGraphics);
                }
            }
        }
    }

    public static int countTooltips(ModifierInstance modifierInstance, boolean isKeyDown) {
        return modifierInstance.effects()
                .stream()
                .mapToInt(it -> ModifierRenderHelper.getTooltipsForEffect(it, isKeyDown, modifierInstance).size())
                .sum();
    }

    public static boolean isKeyDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),
                WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getValue());
    }
}
