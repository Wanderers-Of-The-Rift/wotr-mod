package com.wanderersoftherift.wotr.init.recipe;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            WanderersOfTheRift.MODID);

    public static final Supplier<RecipeType<KeyForgeRecipe>> KEY_FORGE_RECIPE = RECIPE_TYPES
            .register("key_forge_recipe", name -> new RecipeType<>() {
                @Override
                public String toString() {
                    return name.toString();
                }
            });
}
