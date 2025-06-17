package com.wanderersoftherift.wotr.interop.rei.client;

import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.TieredModifier;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

/**
 * REI Renderer for an {@link EssencePredicate}. This communicates the type of essence and the requirements around it
 */
public class ModifierGroupEntryRenderer implements EntryRenderer<RunegemData.ModifierGroup> {
    @Override
    public void render(
            EntryStack<RunegemData.ModifierGroup> entry,
            GuiGraphics graphics,
            Rectangle bounds,
            int mouseX,
            int mouseY,
            float delta) {
        Font font = Minecraft.getInstance().font;

        int offset = 0;
        for (TieredModifier modifier : entry.getValue().modifiers()) {
            graphics.drawString(font, modifier.getName(), bounds.x, bounds.y + offset, ChatFormatting.BLACK.getColor(),
                    false);
        }
    }

    @Override
    public @Nullable Tooltip getTooltip(EntryStack<RunegemData.ModifierGroup> entry, TooltipContext context) {
        return null;
    }
}
