package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
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

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, ModBlocks.ABILITY_BENCH.asItem())
                .pattern("ggg")
                .pattern("w w")
                .pattern("w w")
                .define('g', Items.GLASS)
                .define('w', ItemTags.PLANKS)
                .unlockedBy("has_glass", this.has(Items.GLASS))
                .unlockedBy("has_plank", this.has(ItemTags.PLANKS))
                .save(this.output);

        ItemStack dodgeSkillGem = ModItems.ABILITY_HOLDER.toStack();
        dodgeSkillGem.applyComponents(DataComponentPatch.builder()
                .set(ModDataComponentType.ABILITY.get(),
                        DeferredHolder.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("dash")))
                .build());
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, dodgeSkillGem)
                .pattern("ggg")
                .pattern("gIg")
                .pattern("ggg")
                .define('g', Blocks.GLASS_PANE.asItem())
                .define('I', ItemTags.FOOT_ARMOR)
                .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE.asItem()))
                .save(this.output, "wotr:ability_dash");

        ItemStack fireballSkillGem = ModItems.ABILITY_HOLDER.toStack();
        fireballSkillGem.applyComponents(DataComponentPatch.builder()
                .set(ModDataComponentType.ABILITY.get(),
                        DeferredHolder.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("fireball")))
                .build());
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, fireballSkillGem)
                .pattern("ggg")
                .pattern("gIg")
                .pattern("ggg")
                .define('g', Blocks.GLASS_PANE.asItem())
                .define('I', Items.FLINT_AND_STEEL)
                .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE.asItem()))
                .save(this.output, "wotr:ability_fireball");

        ItemStack healSkillGem = ModItems.ABILITY_HOLDER.toStack();
        healSkillGem.applyComponents(DataComponentPatch.builder()
                .set(ModDataComponentType.ABILITY.get(),
                        DeferredHolder.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("heal")))
                .build());
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, healSkillGem)
                .pattern("ggg")
                .pattern("gIg")
                .pattern("ggg")
                .define('g', Blocks.GLASS_PANE.asItem())
                .define('I', Items.APPLE)
                .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE.asItem()))
                .save(this.output, "wotr:ability_heal");
    }

    // The runner to add to the data generator
    public static class Runner extends RecipeProvider.Runner {
        // Get the parameters from the `GatherDataEvent`s.
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(
                HolderLookup.@NotNull Provider provider,
                @NotNull RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        public @NotNull String getName() {
            return "Wanderers of the Rift's Recipes";
        }
    }
}