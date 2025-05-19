package com.wanderersoftherift.wotr.interop.rei.client;

import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.recipe.WotrRecipeDisplayTypes;
import com.wanderersoftherift.wotr.item.crafting.display.KeyForgeRecipeDisplay;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class WotrREIPluginClient implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new KeyForgeDisplayCategory());
        registry.addWorkstations(WotrDisplayCategories.KEY_FORGE, EntryStacks.of(WotrBlocks.KEY_FORGE.toStack()));
        registry.add(new RuneAnvilDisplayCategory());
        registry.addWorkstations(WotrDisplayCategories.RUNE_ANVIL,
                EntryStacks.of(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.toStack()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.beginRecipeFiller(KeyForgeRecipeDisplay.class)
                .filterType(WotrRecipeDisplayTypes.KEY_FORGE_RECIPE_DISPLAY.get())
                .fill(KeyForgeDisplay::new);
    }

}
