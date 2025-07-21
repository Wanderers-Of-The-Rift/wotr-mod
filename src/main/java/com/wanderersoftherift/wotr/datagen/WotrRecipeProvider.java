package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.crafting.EssencePredicate;
import com.wanderersoftherift.wotr.item.crafting.KeyForgeRecipe;
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
public class WotrRecipeProvider extends RecipeProvider {

    // Construct the provider to run
    protected WotrRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        HolderGetter<Item> getter = this.registries.lookupOrThrow(Registries.ITEM);

        // <editor-fold desc="WotR Blocks and Items">
        // Add recipes for the mod's blocks and items
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, WotrBlocks.RIFT_SPAWNER.asItem())
                .pattern("sss")
                .pattern("sEs")
                .pattern("sss")
                .define('s', Items.STONE)
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", this.has(Items.ENDER_PEARL))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, WotrBlocks.KEY_FORGE.asItem())
                .pattern("   ")
                .pattern("PI ")
                .pattern("BL ")
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_BLOCK)
                .define('B', Items.BLAST_FURNACE)
                .define('P', Items.FLOWER_POT)
                .unlockedBy("has_blast_furnace", this.has(Items.BLAST_FURNACE))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, WotrBlocks.ABILITY_BENCH.asItem())
                .pattern("ggg")
                .pattern("w w")
                .pattern("w w")
                .define('g', Items.GLASS)
                .define('w', ItemTags.PLANKS)
                .unlockedBy("has_glass", this.has(Items.GLASS))
                .unlockedBy("has_plank", this.has(ItemTags.PLANKS))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.asItem())
                .pattern(" e ")
                .pattern("eae")
                .pattern(" e ")
                .define('a', ItemTags.ANVIL)
                .define('e', Items.EMERALD)
                .unlockedBy("has_rune", this.has(WotrItems.RUNEGEM))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, WotrBlocks.QUEST_HUB.asItem())
                .pattern("sGs")
                .pattern("sGs")
                .pattern("sgs")
                .define('G', Items.GOLD_INGOT)
                .define('g', Items.GOLD_NUGGET)
                .define('s', Items.SMOOTH_STONE)
                .unlockedBy("has_gold", this.has(Items.GOLD_INGOT))
                .save(this.output);

        ItemStack dodgeSkillGem = WotrItems.ABILITY_HOLDER.toStack();
        dodgeSkillGem.applyComponents(DataComponentPatch.builder()
                .set(WotrDataComponentType.ABILITY.get(),
                        DeferredHolder.create(WotrRegistries.Keys.ABILITIES, WanderersOfTheRift.id("dash")))
                .build());
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, dodgeSkillGem)
                .pattern("ggg")
                .pattern("gIg")
                .pattern("ggg")
                .define('g', Blocks.GLASS_PANE.asItem())
                .define('I', ItemTags.FOOT_ARMOR)
                .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE.asItem()))
                .save(this.output, "wotr:ability_dash");

        ItemStack fireballSkillGem = WotrItems.ABILITY_HOLDER.toStack();
        fireballSkillGem.applyComponents(DataComponentPatch.builder()
                .set(WotrDataComponentType.ABILITY.get(),
                        DeferredHolder.create(WotrRegistries.Keys.ABILITIES, WanderersOfTheRift.id("fireball")))
                .build());
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, fireballSkillGem)
                .pattern("ggg")
                .pattern("gIg")
                .pattern("ggg")
                .define('g', Blocks.GLASS_PANE.asItem())
                .define('I', Items.FLINT_AND_STEEL)
                .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE.asItem()))
                .save(this.output, "wotr:ability_fireball");

        ItemStack healSkillGem = WotrItems.ABILITY_HOLDER.toStack();
        healSkillGem.applyComponents(DataComponentPatch.builder()
                .set(WotrDataComponentType.ABILITY.get(),
                        DeferredHolder.create(WotrRegistries.Keys.ABILITIES, WanderersOfTheRift.id("heal")))
                .build());
        ShapedRecipeBuilder.shaped(getter, RecipeCategory.MISC, healSkillGem)
                .pattern("ggg")
                .pattern("gIg")
                .pattern("ggg")
                .define('g', Blocks.GLASS_PANE.asItem())
                .define('I', Items.APPLE)
                .unlockedBy("has_glass_pane", this.has(Blocks.GLASS_PANE.asItem()))
                .save(this.output, "wotr:ability_heal");

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVGRAVEL.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.GRAVEL)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_gravel", this.has(Items.GRAVEL))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVSAND.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.SAND)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_sand", this.has(Items.SAND))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVREDSAND.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.RED_SAND)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_red_sand", this.has(Items.RED_SAND))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVWHITECONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.WHITE_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_white_concrete_powder", this.has(Items.WHITE_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVORANGECONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.ORANGE_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_orange_concrete_powder", this.has(Items.ORANGE_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVMAGENTACONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.MAGENTA_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_magenta_concrete_powder", this.has(Items.MAGENTA_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVLIGHTBLUECONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.LIGHT_BLUE_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_light_blue_concrete_powder", this.has(Items.LIGHT_BLUE_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVYELLOWCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.YELLOW_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_yellow_concrete_powder", this.has(Items.YELLOW_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVLIMECONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.LIME_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_lime_concrete_powder", this.has(Items.LIME_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVPINKCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.PINK_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_pink_concrete_powder", this.has(Items.PINK_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVGRAYCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.GRAY_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_gray_concrete_powder", this.has(Items.GRAY_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVLIGHTGRAYCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.LIGHT_GRAY_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_light_gray_concrete_powder", this.has(Items.LIGHT_GRAY_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVCYANCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.CYAN_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_cyan_concrete_powder", this.has(Items.CYAN_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVPURPLECONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.PURPLE_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_purple_concrete_powder", this.has(Items.PURPLE_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVBLUECONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.BLUE_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_blue_concrete_powder", this.has(Items.BLUE_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVBROWNCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.BROWN_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_brown_concrete_powder", this.has(Items.BROWN_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVGREENCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.GREEN_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_green_concrete_powder", this.has(Items.GREEN_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder.shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVREDCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.RED_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_red_concrete_powder", this.has(Items.RED_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);

        ShapedRecipeBuilder
                .shaped(getter, RecipeCategory.BUILDING_BLOCKS, WotrBlocks.NOGRAVBLACKCONCRETEPOWDER.get(), 8)
                .pattern("GGG")
                .pattern("GHG")
                .pattern("GGG")
                .define('G', Items.BLACK_CONCRETE_POWDER)
                .define('H', Items.CHORUS_FRUIT)
                .unlockedBy("has_black_concrete_powder", this.has(Items.BLACK_CONCRETE_POWDER))
                .unlockedBy("has_chorus_fruit", this.has(Items.CHORUS_FRUIT))
                .save(this.output);
        // </editor-fold>

        // <editor-fold desc="WotR Key Forge Recipes">
        // Add recipes for the mod's themes. Order: Cave, processor, alphabetic afterwards.
        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("cave")))
                .setPriority(-1)
                .save(output, WanderersOfTheRift.id("rift_theme_cave"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("processor")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("processor")).setMin(1).build())
                .save(output, WanderersOfTheRift.id("rift_theme_processor"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("buzzy_bees")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("honey")).setMinPercent(25F).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_buzzy_bees"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("color")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("water")).setMinPercent(25F).build())
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("fabric")).setMinPercent(25F).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_color"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("deepfrost")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("nether")).setMinPercent(10f).build())
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("water")).setMinPercent(30f).build())
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("death")).setMinPercent(10f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_deepfrost"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("desert")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("death")).setMinPercent(15F).build())
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("earth")).setMinPercent(35F).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_desert"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("forest")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("plant")).setMinPercent(50f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_forest"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("jungle")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("earth")).setMinPercent(20f).build())
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("life")).setMinPercent(20f).build())
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("plant")).setMinPercent(10f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_jungle"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("meadow")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("earth")).setMinPercent(20f).build())
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("plant")).setMinPercent(20f).build())
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("light")).setMinPercent(10f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_meadow"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("mesa")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("earth")).setMinPercent(50f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_mesa"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("mushroom")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("mushroom")).setMinPercent(50f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_mushroom"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("nether")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("nether")).setMinPercent(50f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_nether"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("noir")))
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("light")).setMinPercent(25f).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_noir"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_THEME.get(),
                        DeferredHolder.create(WotrRegistries.Keys.RIFT_THEMES, WanderersOfTheRift.id("swamp")))
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("water")).setMinPercent(10F).build())
                .withEssenceReq(
                        new EssencePredicate.Builder(WanderersOfTheRift.id("earth")).setMinPercent(40F).build())
                .setPriority(10)
                .save(output, WanderersOfTheRift.id("rift_theme_swamp"));
        // </editor-fold>

        // <editor-fold desc="WotR Key Forge Objectives">
        // Add recipes for the mod's objectives
        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_OBJECTIVE.get(),
                        DeferredHolder.create(WotrRegistries.Keys.OBJECTIVES, WanderersOfTheRift.id("kill")))
                .setPriority(-1)
                .save(output, WanderersOfTheRift.id("rift_objective_kill"));

        KeyForgeRecipe
                .create(WotrDataComponentType.RIFT_OBJECTIVE.get(),
                        DeferredHolder.create(WotrRegistries.Keys.OBJECTIVES, WanderersOfTheRift.id("stealth")))
                .setPriority(1)
                .withEssenceReq(new EssencePredicate.Builder(WanderersOfTheRift.id("dark")).setMinPercent(5f).build())
                .save(output, WanderersOfTheRift.id("rift_objective_stealth"));
        // </editor-fold>

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
            return new WotrRecipeProvider(provider, output);
        }

        @Override
        public @NotNull String getName() {
            return "Wanderers of the Rift's Recipes";
        }
    }
}
