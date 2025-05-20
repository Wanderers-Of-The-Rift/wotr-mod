package com.wanderersoftherift.wotr.interop.rei.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * REI Renderer for an {@link EssencePredicate}. This communicates the type of essence and the requirements around it
 */
public class EssenceEntryRenderer implements EntryRenderer<EssencePredicate> {
    @Override
    public void render(
            EntryStack<EssencePredicate> entry,
            GuiGraphics graphics,
            Rectangle bounds,
            int mouseX,
            int mouseY,
            float delta) {
        Font font = Minecraft.getInstance().font;
        Component essenceType = getEssenceTypeComponent(entry.getValue().essenceType());
        int offset = 0;
        if (entry.getValue().minPercent() > 0) {
            graphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("rei", "percent.min"), essenceType,
                            entry.getValue().minPercent()),
                    bounds.x, bounds.y + offset, ChatFormatting.DARK_GRAY.getColor(), false);
            offset += font.lineHeight;
        }
        if (entry.getValue().maxPercent() < 100) {
            graphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("rei", "percent.max"), essenceType,
                            entry.getValue().maxPercent()),
                    bounds.x, bounds.y + offset, ChatFormatting.DARK_GRAY.getColor(), false);
            offset += font.lineHeight;
        }
        if (entry.getValue().min() > 0) {
            graphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("rei", "absolute.min"), essenceType,
                            entry.getValue().min()),
                    bounds.x, bounds.y + offset, ChatFormatting.DARK_GRAY.getColor(), false);
            offset += font.lineHeight;
        }
        if (entry.getValue().max() < Integer.MAX_VALUE) {
            graphics.drawString(font,
                    Component.translatable(WanderersOfTheRift.translationId("rei", "absolute.max"), essenceType,
                            entry.getValue().max()),
                    bounds.x, bounds.y + offset, ChatFormatting.DARK_GRAY.getColor(), false);
            offset += font.lineHeight;
        }
    }

    private Component getEssenceTypeComponent(ResourceLocation essenceType) {
        return Component.translatable(
                EssenceValue.ESSENCE_TYPE_PREFIX + "." + essenceType.getNamespace() + "." + essenceType.getPath());
    }

    @Override
    public @Nullable Tooltip getTooltip(EntryStack<EssencePredicate> entry, TooltipContext context) {
        return null;
    }
}
