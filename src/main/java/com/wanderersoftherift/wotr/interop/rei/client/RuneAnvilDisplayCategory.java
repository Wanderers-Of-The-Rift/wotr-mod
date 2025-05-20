package com.wanderersoftherift.wotr.interop.rei.client;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.modifier.TieredModifier;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class RuneAnvilDisplayCategory implements DisplayCategory<RuneAnvilDisplay> {

    @Override
    public CategoryIdentifier<? extends RuneAnvilDisplay> getCategoryIdentifier() {
        return WotrDisplayCategories.RUNE_ANVIL;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(WanderersOfTheRift.translationId("block", "rune_anvil"));
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.toStack());
    }

    @Override
    public int getDisplayWidth(RuneAnvilDisplay display) {
        return 175;
    }

    @Override
    public int getDisplayHeight() {
        return 100;
    }

    @Override
    public List<Widget> setupDisplay(RuneAnvilDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int offset = 0;
        for (EntryIngredient inputEntry : display.getInputEntries()) {
            widgets.add(
                    Widgets.createSlot(new Point(bounds.x + 5, bounds.y + 5 + offset)).entries(inputEntry).markInput());
            offset += 18;
        }
        offset = 0;
        for (EntryIngredient outputEntry : display.getOutputEntries()) {
            for (EntryStack<?> entryStack : outputEntry) {
                if (entryStack.getValue() instanceof RunegemData.ModifierGroup group) {
                    for (TieredModifier modifier : group.modifiers()) {
                        widgets.add(
                                Widgets.createLabel(new Point(bounds.x + 24, bounds.y + 6 + offset), modifier.getName())
                                        .leftAligned()
                                        .shadow(false)
                                        .color(ChatFormatting.BLACK.getColor(), ChatFormatting.WHITE.getColor()));
                        offset += 12;
                    }
                }
            }

        }
        return widgets;
    }
}
