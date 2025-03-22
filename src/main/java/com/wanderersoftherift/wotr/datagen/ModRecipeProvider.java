package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.init.ModBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/* Handles Data Generation for Recipes of the Wotr mod */
public class ModRecipeProvider extends RecipeProvider {

    // Construct the provider to run
    protected ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {

        HolderGetter<Item> getter = this.registries.lookupOrThrow(Registries.ITEM);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, ModBlocks.RIFT_SPAWNER.asItem())
                .pattern("sss")
                .pattern("sEs")
                .pattern("sss")
                .define('s', Items.STONE)
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", this.has(Items.ENDER_PEARL))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, ModBlocks.KEY_FORGE.asItem())
                .pattern("   ")
                .pattern(" E ")
                .pattern(" a ")
                .define('a', ItemTags.ANVIL)
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", this.has(Items.ENDER_PEARL))
                .unlockedBy("has_anvil", this.has(ItemTags.ANVIL))
                .save(this.output);
    }

    // The runner to add to the data generator
    public static class Runner extends RecipeProvider.Runner {
        // Get the parameters from the `GatherDataEvent`s.
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public @NotNull String getName() {
            return "Dimension Delver's Recipes";
        }
    }
}