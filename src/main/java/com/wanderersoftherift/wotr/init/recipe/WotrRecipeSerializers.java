package com.wanderersoftherift.wotr.init.recipe;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, WanderersOfTheRift.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<KeyForgeRecipe>> KEY_FORGE_RECIPE = RECIPE_SERIALIZERS
            .register("key_forge_recipe", KeyForgeRecipe.Serializer::new);
}
