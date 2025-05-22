package com.wanderersoftherift.wotr.init.recipe;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrRecipeCategories {
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister
            .create(Registries.RECIPE_BOOK_CATEGORY, WanderersOfTheRift.MODID);

    public static final Supplier<RecipeBookCategory> KEYFORGE_RECIPE = RECIPE_BOOK_CATEGORIES.register("key_forge",
            RecipeBookCategory::new);
}
