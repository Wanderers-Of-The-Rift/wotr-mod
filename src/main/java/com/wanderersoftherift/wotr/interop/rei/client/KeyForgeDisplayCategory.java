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
import net.minecraft.network.chat.Component;

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
