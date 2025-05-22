package com.wanderersoftherift.wotr.init.recipe;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.display.KeyForgeRecipeDisplay;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRecipeDisplayTypes {
    public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAY_TYPES = DeferredRegister
            .create(Registries.RECIPE_DISPLAY, WanderersOfTheRift.MODID);

    public static final Supplier<RecipeDisplay.Type<KeyForgeRecipeDisplay>> KEY_FORGE_RECIPE_DISPLAY = RECIPE_DISPLAY_TYPES
            .register("key_forge_recipe", () -> new RecipeDisplay.Type<>(KeyForgeRecipeDisplay.MAP_CODEC,
                    KeyForgeRecipeDisplay.STREAM_CODEC));
}
