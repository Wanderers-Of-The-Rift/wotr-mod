package com.wanderersoftherift.wotr.interop.rei.common;

import com.wanderersoftherift.wotr.init.recipe.WotrRecipeTypes;
import com.wanderersoftherift.wotr.interop.rei.client.KeyForgeDisplay;
import com.wanderersoftherift.wotr.interop.rei.client.RuneAnvilDisplay;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.forge.REIPluginCommon;

@REIPluginCommon
public class WotrREIPluginCommon implements REICommonPlugin {

    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(WotrEntryTypes.ESSENCE, new EssenceEntryDefinition());
        registry.register(WotrEntryTypes.MODIFIER_GROUP, new ModifierGroupEntryDefinition());
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(WotrDisplays.KEY_FORGE_DISPLAY, KeyForgeDisplay.SERIALIZER);
        registry.register(WotrDisplays.RUNE_ANVIL_DISPLAY, RuneAnvilDisplay.SERIALIZER);
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(KeyForgeRecipe.class)
                .filterType(WotrRecipeTypes.KEY_FORGE_RECIPE.get())
                .fill(recipe -> new KeyForgeDisplay(recipe.value(), recipe.id().location()));
    }

}
