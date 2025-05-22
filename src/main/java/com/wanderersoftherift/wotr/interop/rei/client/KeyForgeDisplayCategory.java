package com.wanderersoftherift.wotr.interop.rei.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * REI Category for the KeyForge and its recipes. This handles rendering of the REI panel for a KeyForge recipe
 */
public class KeyForgeDisplayCategory implements DisplayCategory<KeyForgeDisplay> {
    @Override
    public CategoryIdentifier<? extends KeyForgeDisplay> getCategoryIdentifier() {
        return WotrDisplayCategories.KEY_FORGE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(WanderersOfTheRift.translationId("block", "key_forge"));
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(WotrBlocks.KEY_FORGE.toStack());
    }

    @Override
    public int getDisplayWidth(KeyForgeDisplay display) {
        return 200;
    }

    @Override
    public List<Widget> setupDisplay(KeyForgeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(
                Widgets.createResultSlotBackground(new Point(bounds.x + bounds.width - 30, bounds.getCenterY() - 8)));

        int offset = 0;
        if (display.getOutputEntries().get(0).get(0).getValue() instanceof ItemStack stack) {
            List<Component> tooltipLines = stack.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level),
                    null, TooltipFlag.NORMAL);
            if (tooltipLines.size() > 1) {
                widgets.add(Widgets
                        .createLabel(new Point(bounds.x + 5, bounds.y + 5 + offset), tooltipLines.get(1).plainCopy())
                        .leftAligned()
                        .shadow(false)
                        .color(ChatFormatting.BLACK.getColor(), ChatFormatting.WHITE.getColor()));
                offset += 12;
            }
        }

        for (EntryIngredient inputEntry : display.getInputEntries()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 5, bounds.y + 5 + offset))
                    .entries(inputEntry)
                    .disableBackground()
                    .disableHighlight()
                    .markInput());
            offset += 10;
        }
        widgets.add(Widgets.createSlot(new Point(bounds.x + bounds.width - 30, bounds.getCenterY() - 8))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());
        return widgets;
    }
}
