package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataMaps;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Data gen for Data Maps - these are additional information that can be attached to registries, similar to tags but
 * with more content
 */
public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        ResourceLocation animal = WanderersOfTheRift.id("animal");
        ResourceLocation plant = WanderersOfTheRift.id("plant");

        ResourceLocation life = WanderersOfTheRift.id("life");
        ResourceLocation death = WanderersOfTheRift.id("death");

        ResourceLocation light = WanderersOfTheRift.id("light");
        ResourceLocation dark = WanderersOfTheRift.id("dark");

        ResourceLocation order = WanderersOfTheRift.id("order");
        ResourceLocation chaos = WanderersOfTheRift.id("chaos");

        ResourceLocation earth = WanderersOfTheRift.id("earth");
        ResourceLocation fire = WanderersOfTheRift.id("fire");
        ResourceLocation water = WanderersOfTheRift.id("water");
        ResourceLocation air = WanderersOfTheRift.id("air");

        ResourceLocation time = WanderersOfTheRift.id("time");
        ResourceLocation space = WanderersOfTheRift.id("space");

        ResourceLocation metal = WanderersOfTheRift.id("metal");
        ResourceLocation fabric = WanderersOfTheRift.id("fabric");
        ResourceLocation crystal = WanderersOfTheRift.id("crystal");
        ResourceLocation power = WanderersOfTheRift.id("power");
        ResourceLocation knowledge = WanderersOfTheRift.id("knowledge");

        ResourceLocation nether = WanderersOfTheRift.id("nether");
        ResourceLocation end = WanderersOfTheRift.id("end");

        // Bee stuff
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.BEEHIVE.builtInRegistryHolder(), new EssenceValue(order, 1, animal, 1), false)
                .add(Items.BEE_NEST.builtInRegistryHolder(), new EssenceValue(order, 1, animal, 1), false)
                .add(Items.HONEY_BLOCK.builtInRegistryHolder(), new EssenceValue(water, 1, life, 1), false)
                .add(Items.HONEY_BOTTLE.builtInRegistryHolder(), new EssenceValue(water, 1, life, 1), false)
                .add(Items.HONEYCOMB.builtInRegistryHolder(), new EssenceValue(order, 1, life, 1), false)
                .add(Items.HONEYCOMB_BLOCK.builtInRegistryHolder(), new EssenceValue(order, 1, life, 1), false);

        // Flowers
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.DANDELION.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.POPPY.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.ALLIUM.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.AZURE_BLUET.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.RED_TULIP.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.ORANGE_TULIP.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.WHITE_TULIP.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.PINK_TULIP.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.OXEYE_DAISY.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.CORNFLOWER.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.LILY_OF_THE_VALLEY.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.TORCHFLOWER.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.OPEN_EYEBLOSSOM.builtInRegistryHolder(), new EssenceValue(plant, 1, light, 1), false)
                .add(Items.CLOSED_EYEBLOSSOM.builtInRegistryHolder(), new EssenceValue(plant, 1, dark, 1), false)
                .add(Items.WITHER_ROSE.builtInRegistryHolder(), new EssenceValue(plant, 1, death, 1), false)
                .add(Tags.Items.FLOWERS_TALL, new EssenceValue(plant, 1), false)
                .add(Items.BLUE_ORCHID.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.SPORE_BLOSSOM.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.PINK_PETALS.builtInRegistryHolder(), new EssenceValue(plant, 1), false);

        // Trees
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.LEAVES, new EssenceValue(plant, 1), false)
                .add(ItemTags.SAPLINGS, new EssenceValue(plant, 1, life, 1), false)
                .add(Items.STICK.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(ItemTags.OAK_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.SPRUCE_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.BIRCH_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.JUNGLE_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.ACACIA_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.DARK_OAK_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.MANGROVE_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.CHERRY_LOGS, new EssenceValue(plant, 1), false)
                .add(ItemTags.PALE_OAK_LOGS, new EssenceValue(plant, 1), false);

        // Nether Plants
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.NETHER_WART.builtInRegistryHolder(), new EssenceValue(nether, 1, plant, 1), false)
                .add(ItemTags.WART_BLOCKS, new EssenceValue(plant, 1, nether, 1), false)
                .add(ItemTags.WARPED_STEMS, new EssenceValue(plant, 1, nether, 1), false)
                .add(ItemTags.CRIMSON_STEMS, new EssenceValue(plant, 1, nether, 1), false)
                .add(Items.CRIMSON_ROOTS.builtInRegistryHolder(), new EssenceValue(plant, 1, nether, 1), false)
                .add(Items.WARPED_ROOTS.builtInRegistryHolder(), new EssenceValue(plant, 1, nether, 1), false)
                .add(Items.NETHER_SPROUTS.builtInRegistryHolder(), new EssenceValue(plant, 1, nether, 1), false)
                .add(Items.WEEPING_VINES.builtInRegistryHolder(), new EssenceValue(plant, 1, nether, 1), false)
                .add(Items.TWISTING_VINES.builtInRegistryHolder(), new EssenceValue(plant, 1, nether, 1), false);

        // Wood
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.PLANKS, new EssenceValue(plant, 1), false)
                .add(ItemTags.WOODEN_SLABS, new EssenceValue(plant, 1), false)
                .add(ItemTags.WOODEN_STAIRS, new EssenceValue(plant, 1), false);

        // Bamboo
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.BAMBOO.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(ItemTags.BAMBOO_BLOCKS, new EssenceValue(plant, 1), false)
                .add(Items.BAMBOO_MOSAIC.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.BAMBOO_MOSAIC_SLAB.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.BAMBOO_MOSAIC_STAIRS.builtInRegistryHolder(), new EssenceValue(plant, 1), false);

        // Crops and Berries
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.APPLE.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.BEETROOT.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.CACTUS.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.CARROT.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.COCOA_BEANS.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.MELON.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.MELON_SLICE.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.PUMPKIN.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.CARVED_PUMPKIN.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.POTATO.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.POISONOUS_POTATO.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.SUGAR_CANE.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.WHEAT.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.HAY_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.SWEET_BERRIES.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.GLOW_BERRIES.builtInRegistryHolder(), new EssenceValue(plant, 1, light, 1), false)
                .add(Tags.Items.SEEDS, new EssenceValue(plant, 1, life, 1), false);

        // Fungi
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.MUSHROOMS, new EssenceValue(life, 1), false)
                .add(Items.MUSHROOM_STEM.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.BROWN_MUSHROOM_BLOCK.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.MYCELIUM.builtInRegistryHolder(), new EssenceValue(earth, 1, life, 1), false)
                .add(Items.RED_MUSHROOM_BLOCK.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.CRIMSON_NYLIUM.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Items.WARPED_NYLIUM.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Items.CRIMSON_FUNGUS.builtInRegistryHolder(), new EssenceValue(life, 1, nether, 1), false)
                .add(Items.WARPED_FUNGUS.builtInRegistryHolder(), new EssenceValue(life, 1, nether, 1), false)
                .add(Items.GLOW_LICHEN.builtInRegistryHolder(), new EssenceValue(life, 1, light, 1), false);

        // Grasses, ground & wall covers
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.VINE.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.GRASS_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, earth, 1), false)
                .add(Items.PODZOL.builtInRegistryHolder(), new EssenceValue(plant, 1, earth, 1), false)
                .add(Items.SHORT_GRASS.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.TALL_GRASS.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.FERN.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.LARGE_FERN.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.DEAD_BUSH.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.MOSS_CARPET.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.MOSS_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.PALE_MOSS_CARPET.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.PALE_MOSS_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.PALE_HANGING_MOSS.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.BIG_DRIPLEAF.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.SMALL_DRIPLEAF.builtInRegistryHolder(), new EssenceValue(plant, 1), false);

        // Roots
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.ROOTED_DIRT.builtInRegistryHolder(), new EssenceValue(plant, 1, earth, 1), false)
                .add(Items.MANGROVE_ROOTS.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.MUDDY_MANGROVE_ROOTS.builtInRegistryHolder(), new EssenceValue(plant, 1, earth, 1), false)
                .add(Items.HANGING_ROOTS.builtInRegistryHolder(), new EssenceValue(plant, 1), false);

        // Creaking related
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.CREAKING_HEART.builtInRegistryHolder(), new EssenceValue(life, 1, plant, 1), false)
                .add(Items.RESIN_CLUMP.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.RESIN_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.RESIN_BRICK.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.RESIN_BRICKS.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.RESIN_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.RESIN_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.RESIN_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1), false)
                .add(Items.CHISELED_RESIN_BRICKS.builtInRegistryHolder(), new EssenceValue(plant, 1, crystal, 1),
                        false);

        // End flora
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.CHORUS_PLANT.builtInRegistryHolder(), new EssenceValue(end, 1, plant, 1), false)
                .add(Items.CHORUS_FLOWER.builtInRegistryHolder(), new EssenceValue(end, 1, plant, 1), false)
                .add(Items.CHORUS_FRUIT.builtInRegistryHolder(), new EssenceValue(end, 1, space, 1, plant, 1), false)
                .add(Items.POPPED_CHORUS_FRUIT.builtInRegistryHolder(), new EssenceValue(end, 1, plant, 1), false);

        // Skulk
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.SCULK.builtInRegistryHolder(), new EssenceValue(dark, 1, life, 1), false)
                .add(Items.SCULK_CATALYST.builtInRegistryHolder(), new EssenceValue(dark, 1, life, 1), false)
                .add(Items.SCULK_SENSOR.builtInRegistryHolder(), new EssenceValue(dark, 1, life, 1), false)
                .add(Items.SCULK_VEIN.builtInRegistryHolder(), new EssenceValue(dark, 1, life, 1), false)
                .add(Items.SCULK_SHRIEKER.builtInRegistryHolder(), new EssenceValue(dark, 1, life, 1), false)
                .add(Items.CALIBRATED_SCULK_SENSOR.builtInRegistryHolder(), new EssenceValue(dark, 1, crystal, 1),
                        false);

        // Water life
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.PUFFERFISH_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.SALMON_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.COD_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.TROPICAL_FISH_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.AXOLOTL_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.TADPOLE_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.NAUTILUS_SHELL.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)

                .add(Items.SPONGE.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.WET_SPONGE.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.SEAGRASS.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.SEA_PICKLE.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.KELP.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DRIED_KELP.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.DRIED_KELP_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.LILY_PAD.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)

                .add(Items.BRAIN_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.BRAIN_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.BRAIN_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.BUBBLE_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.BUBBLE_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.BUBBLE_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.FIRE_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.FIRE_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.FIRE_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.HORN_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.HORN_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.HORN_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.TUBE_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.TUBE_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.TUBE_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)

                .add(Items.DEAD_BRAIN_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_BRAIN_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_BRAIN_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_BUBBLE_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_BUBBLE_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_BUBBLE_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_FIRE_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_FIRE_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_FIRE_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_HORN_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_HORN_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_HORN_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_TUBE_CORAL.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_TUBE_CORAL_BLOCK.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false)
                .add(Items.DEAD_TUBE_CORAL_FAN.builtInRegistryHolder(), new EssenceValue(plant, 1, water, 1), false);

        // Water
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.WATER_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.POWDER_SNOW_BUCKET.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.SNOWBALL.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.SNOW.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.SNOW_BLOCK.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.ICE.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.BLUE_ICE.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.PACKED_ICE.builtInRegistryHolder(), new EssenceValue(water, 1), false);

        // Meats
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.FOODS_RAW_MEAT, new EssenceValue(animal, 1), false)
                .add(Tags.Items.FOODS_RAW_FISH, new EssenceValue(animal, 1, water, 1), false);

        // Animal products
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.ARMADILLO_SCUTE.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.TURTLE_SCUTE.builtInRegistryHolder(), new EssenceValue(animal, 1, water, 1), false)
                .add(Items.FEATHER.builtInRegistryHolder(), new EssenceValue(animal, 1, air, 1), false)
                .add(Items.LEATHER.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.RABBIT_HIDE.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.RABBIT_FOOT.builtInRegistryHolder(), new EssenceValue(animal, 1, chaos, 1), false)
                .add(Items.MILK_BUCKET.builtInRegistryHolder(), new EssenceValue(animal, 1, water, 1), false)
                .add(Items.INK_SAC.builtInRegistryHolder(), new EssenceValue(animal, 1, water, 1), false)
                .add(Items.GLOW_INK_SAC.builtInRegistryHolder(), new EssenceValue(animal, 1, light, 1), false);

        // Foods
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.SUGAR.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.CAKE.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.COOKIE.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Tags.Items.FOODS_COOKED_FISH, new EssenceValue(life, 1, animal, 1), false)
                .add(Tags.Items.FOODS_COOKED_MEAT, new EssenceValue(life, 1, animal, 1), false)
                .add(Items.PUMPKIN_PIE.builtInRegistryHolder(), new EssenceValue(life, 1, plant, 1), false)
                .add(Items.MUSHROOM_STEW.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.SUSPICIOUS_STEW.builtInRegistryHolder(), new EssenceValue(chaos, 1), false)
                .add(Items.BREAD.builtInRegistryHolder(), new EssenceValue(life, 1), false)
                .add(Items.GOLDEN_CARROT.builtInRegistryHolder(), new EssenceValue(life, 1, metal, 1), false)
                .add(Items.GOLDEN_APPLE.builtInRegistryHolder(), new EssenceValue(life, 1, metal, 1), false)
                .add(Items.GLISTERING_MELON_SLICE.builtInRegistryHolder(), new EssenceValue(life, 1, metal, 1), false)
                .add(Items.FERMENTED_SPIDER_EYE.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.BAKED_POTATO.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.RABBIT_STEW.builtInRegistryHolder(), new EssenceValue(animal, 1, life, 1), false)
                .add(Items.BEETROOT_SOUP.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false);

        // Eggs
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                // TODO: 1.21.5 replace with eggs itemtag
                .add(Items.EGG.builtInRegistryHolder(), new EssenceValue(animal, 1, life, 1), false)
                .add(Items.TURTLE_EGG.builtInRegistryHolder(), new EssenceValue(animal, 1, life, 1), false)
                .add(Items.SNIFFER_EGG.builtInRegistryHolder(), new EssenceValue(animal, 1, life, 1), false)
                .add(Items.DRAGON_EGG.builtInRegistryHolder(), new EssenceValue(end, 1, life, 1), false);

        // Mob drops
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.SLIME_BALL.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.SLIME_BLOCK.builtInRegistryHolder(), new EssenceValue(water, 1, animal, 1), false)
                .add(Items.SPIDER_EYE.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.COBWEB.builtInRegistryHolder(), new EssenceValue(animal, 1, fabric, 1), false)
                .add(Items.ROTTEN_FLESH.builtInRegistryHolder(), new EssenceValue(animal, 1, death, 1), false)
                .add(Items.PHANTOM_MEMBRANE.builtInRegistryHolder(), new EssenceValue(chaos, 1), false)
                .add(Items.BREEZE_ROD.builtInRegistryHolder(), new EssenceValue(air, 1), false)
                .add(Items.BLAZE_ROD.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.BLAZE_POWDER.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(ItemTags.SKULLS, new EssenceValue(death, 1), false)
                .add(Items.GHAST_TEAR.builtInRegistryHolder(), new EssenceValue(nether, 1, death, 1), false)
                .add(Items.ENDER_PEARL.builtInRegistryHolder(), new EssenceValue(end, 1, space, 1), false)
                .add(Items.ENDER_EYE.builtInRegistryHolder(), new EssenceValue(space, 1, knowledge, 1), false)
                .add(Items.MAGMA_CREAM.builtInRegistryHolder(), new EssenceValue(life, 1, fire, 1), false)
                .add(Items.SHULKER_SHELL.builtInRegistryHolder(), new EssenceValue(animal, 1, end, 1), false);

        // Bones
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.BONE.builtInRegistryHolder(), new EssenceValue(animal, 1, death, 1), false)
                .add(Items.BONE_BLOCK.builtInRegistryHolder(), new EssenceValue(animal, 1, death, 1), false)
                .add(Items.BONE_MEAL.builtInRegistryHolder(), new EssenceValue(animal, 1, death, 1), false);

        // Textiles
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.STRING.builtInRegistryHolder(), new EssenceValue(fabric, 1), false)
                .add(ItemTags.WOOL, new EssenceValue(fabric, 1), false)
                .add(ItemTags.WOOL_CARPETS, new EssenceValue(fabric, 1), false)
                .add(ItemTags.BANNERS, new EssenceValue(fabric, 1, order, 1), false)
                .add(ItemTags.BEDS, new EssenceValue(fabric, 1, order, 1), false)
                .add(Tags.Items.DYES, new EssenceValue(order, 1, water, 1), false)
                .add(Items.FLOWER_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.CREEPER_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.SKULL_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.MOJANG_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.GLOBE_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.PIGLIN_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.FLOW_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.GUSTER_BANNER_PATTERN.builtInRegistryHolder(), new EssenceValue(fabric, 1, knowledge, 1),
                        false)
                .add(Items.FIELD_MASONED_BANNER_PATTERN.builtInRegistryHolder(),
                        new EssenceValue(fabric, 1, knowledge, 1), false)
                .add(Items.BORDURE_INDENTED_BANNER_PATTERN.builtInRegistryHolder(),
                        new EssenceValue(fabric, 1, knowledge, 1), false);

        // Workstations
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.CRAFTING_TABLE.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(ItemTags.ANVIL, new EssenceValue(metal, 1, order, 1), false)
                .add(Items.FURNACE.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.BLAST_FURNACE.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.SMOKER.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.CRAFTER.builtInRegistryHolder(), new EssenceValue(order, 1, power, 1), false)
                .add(Items.BREWING_STAND.builtInRegistryHolder(), new EssenceValue(order, 1, knowledge, 1), false)
                .add(Items.CAULDRON.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.LOOM.builtInRegistryHolder(), new EssenceValue(fabric, 1, order, 1), false)
                .add(Items.COMPOSTER.builtInRegistryHolder(), new EssenceValue(life, 1, plant, 1), false)
                .add(Items.CARTOGRAPHY_TABLE.builtInRegistryHolder(), new EssenceValue(space, 1, order, 1), false)
                .add(Items.FLETCHING_TABLE.builtInRegistryHolder(), new EssenceValue(air, 1, order, 1), false)
                .add(Items.GRINDSTONE.builtInRegistryHolder(), new EssenceValue(earth, 1, chaos, 1), false)
                .add(Items.SMITHING_TABLE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.STONECUTTER.builtInRegistryHolder(), new EssenceValue(earth, 1, order, 1), false)

        ;

        // Transportation
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.BOATS, new EssenceValue(order, 1, water, 1), false)
                .add(ItemTags.CHEST_BOATS, new EssenceValue(order, 1, water, 1), false)
                .add(Items.MINECART.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.FURNACE_MINECART.builtInRegistryHolder(), new EssenceValue(order, 1, fire, 1), false)
                .add(Items.CHEST_MINECART.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.TNT_MINECART.builtInRegistryHolder(), new EssenceValue(order, 1, fire, 1), false)
                .add(Items.HOPPER_MINECART.builtInRegistryHolder(), new EssenceValue(order, 1, metal, 1), false);

        // Dirt
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.SAND, new EssenceValue(earth, 1), false)
                .add(Items.DIRT.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.COARSE_DIRT.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.MUD.builtInRegistryHolder(), new EssenceValue(earth, 1, water, 1), false)
                .add(Items.PACKED_MUD.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.GRAVEL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.SUSPICIOUS_GRAVEL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CLAY_BALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CLAY.builtInRegistryHolder(), new EssenceValue(earth, 1), false);

        // Stone
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.FLINT.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Tags.Items.STONES, new EssenceValue(earth, 1), false)
                .add(Items.STONE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.STONE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Items.ANDESITE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.ANDESITE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.ANDESITE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BLACKSTONE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BLACKSTONE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BLACKSTONE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BLACKSTONE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.GILDED_BLACKSTONE.builtInRegistryHolder(), new EssenceValue(earth, 1, metal, 1), false)
                .add(Items.DIORITE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DIORITE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DIORITE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.GRANITE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.GRANITE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.GRANITE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.TUFF_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.TUFF_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.TUFF_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Tags.Items.COBBLESTONES_NORMAL, new EssenceValue(earth, 1), false)
                .add(Items.COBBLESTONE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.COBBLESTONE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.COBBLESTONE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Tags.Items.COBBLESTONES_MOSSY, new EssenceValue(earth, 1, plant, 1), false)
                .add(Items.MOSSY_COBBLESTONE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1), false)
                .add(Items.MOSSY_COBBLESTONE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1),
                        false)
                .add(Items.MOSSY_COBBLESTONE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1), false)

                .add(Tags.Items.COBBLESTONES_DEEPSLATE, new EssenceValue(earth, 1), false)
                .add(Items.COBBLED_DEEPSLATE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.COBBLED_DEEPSLATE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.COBBLED_DEEPSLATE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Items.SMOOTH_STONE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.SMOOTH_STONE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Items.CHISELED_DEEPSLATE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Items.CALCITE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POINTED_DRIPSTONE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DRIPSTONE_BLOCK.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Items.BASALT.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.SMOOTH_BASALT.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.POLISHED_BASALT.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)

                .add(Items.POLISHED_ANDESITE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_ANDESITE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_ANDESITE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CHISELED_POLISHED_BLACKSTONE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DIORITE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DIORITE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DIORITE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_GRANITE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_GRANITE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_GRANITE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DEEPSLATE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DEEPSLATE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DEEPSLATE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_DEEPSLATE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_TUFF.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_TUFF_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_TUFF_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_TUFF_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CHISELED_TUFF.builtInRegistryHolder(), new EssenceValue(earth, 1), false)

                .add(Items.OBSIDIAN.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.CRYING_OBSIDIAN.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.MAGMA_BLOCK.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)

                .add(Tags.Items.SANDSTONE_BLOCKS, new EssenceValue(earth, 1), false)
                .add(Tags.Items.SANDSTONE_SLABS, new EssenceValue(earth, 1), false)
                .add(Tags.Items.SANDSTONE_STAIRS, new EssenceValue(earth, 1), false)
                .add(Items.SANDSTONE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.RED_SANDSTONE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(ItemTags.TERRACOTTA, new EssenceValue(earth, 1), false)
                .add(Tags.Items.GLAZED_TERRACOTTAS, new EssenceValue(earth, 1), false)
                .add(Tags.Items.CONCRETES, new EssenceValue(earth, 1), false)
                .add(Tags.Items.CONCRETE_POWDERS, new EssenceValue(earth, 1), false);

        // Bricks
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.BRICKS_NORMAL, new EssenceValue(earth, 1), false)
                .add(Items.BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.MUD_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.MUD_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.MUD_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.MUD_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CRACKED_DEEPSLATE_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_TILES.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_TILE_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_TILE_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.DEEPSLATE_TILE_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CRACKED_DEEPSLATE_TILES.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(ItemTags.STONE_BRICKS, new EssenceValue(earth, 1), false)
                .add(Items.STONE_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.STONE_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.STONE_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.MOSSY_STONE_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1), false)
                .add(Items.MOSSY_STONE_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1), false)
                .add(Items.MOSSY_STONE_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1),
                        false)
                .add(Items.MOSSY_STONE_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1), false)
                .add(Items.TUFF_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.TUFF_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.TUFF_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.TUFF_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CHISELED_TUFF_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1),
                        false);

        // Nether Stone
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.NETHERRACK.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Tags.Items.BRICKS_NETHER, new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.NETHER_BRICKS.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.CRACKED_NETHER_BRICKS.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.NETHER_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Items.NETHER_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Items.NETHER_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Items.CHISELED_NETHER_BRICKS.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.RED_NETHER_BRICKS.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.RED_NETHER_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.RED_NETHER_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1),
                        false)
                .add(Items.RED_NETHER_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(nether, 1, earth, 1), false)
                .add(Items.SOUL_SAND.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false)
                .add(Items.SOUL_SOIL.builtInRegistryHolder(), new EssenceValue(earth, 1, nether, 1), false);

        // End Stone
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.PURPUR_BLOCK.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.PURPUR_PILLAR.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.PURPUR_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.PURPUR_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.END_STONE.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.END_STONE_BRICKS.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.END_STONE_BRICK_SLAB.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.END_STONE_BRICK_STAIRS.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false)
                .add(Items.END_STONE_BRICK_WALL.builtInRegistryHolder(), new EssenceValue(earth, 1, end, 1), false);

        // Glass
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.GLASS_BLOCKS_CHEAP, new EssenceValue(earth, 1, crystal, 1), false)
                .add(Tags.Items.GLASS_PANES, new EssenceValue(earth, 1, crystal, 1), false)
                .add(Items.TINTED_GLASS.builtInRegistryHolder(), new EssenceValue(earth, 1, crystal, 1), false)
                .add(Items.GLASS_BOTTLE.builtInRegistryHolder(), new EssenceValue(earth, 1, crystal, 1), false);

        // Ores
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.COAL_ORES, new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.COAL.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.CHARCOAL.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(Items.COAL_BLOCK.builtInRegistryHolder(), new EssenceValue(earth, 1, fire, 1), false)
                .add(ItemTags.COPPER_ORES, new EssenceValue(earth, 1, metal, 1), false)
                .add(Items.GOLD_ORE.builtInRegistryHolder(), new EssenceValue(earth, 1, metal, 1), false)
                .add(Items.DEEPSLATE_GOLD_ORE.builtInRegistryHolder(), new EssenceValue(earth, 1, metal, 1), false)
                .add(Items.NETHER_GOLD_ORE.builtInRegistryHolder(), new EssenceValue(nether, 1, metal, 1), false)
                .add(ItemTags.DIAMOND_ORES, new EssenceValue(earth, 1, crystal, 1), false)
                .add(ItemTags.EMERALD_ORES, new EssenceValue(earth, 1, crystal, 1), false)
                .add(ItemTags.IRON_ORES, new EssenceValue(earth, 1, metal, 1), false)
                .add(ItemTags.LAPIS_ORES, new EssenceValue(earth, 1, crystal, 1), false)
                .add(Tags.Items.ORES_QUARTZ, new EssenceValue(nether, 1, crystal, 1), false)
                .add(Tags.Items.ORES_REDSTONE, new EssenceValue(earth, 1, power, 1), false)
                .add(Items.ANCIENT_DEBRIS.builtInRegistryHolder(), new EssenceValue(nether, 1, metal, 1), false);

        // Copper
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.RAW_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.RAW_COPPER_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Tags.Items.INGOTS_COPPER, new EssenceValue(metal, 1), false)
                .add(Items.COPPER_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.EXPOSED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WEATHERED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.OXIDIZED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.EXPOSED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WEATHERED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.OXIDIZED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.EXPOSED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WEATHERED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.OXIDIZED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.EXPOSED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WEATHERED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.OXIDIZED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.EXPOSED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WEATHERED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.OXIDIZED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)

                .add(Items.WAXED_COPPER_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_EXPOSED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_WEATHERED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_OXIDIZED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_EXPOSED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_WEATHERED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_OXIDIZED_CHISELED_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_EXPOSED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_WEATHERED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_OXIDIZED_CUT_COPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_EXPOSED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_WEATHERED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_EXPOSED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_WEATHERED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS.builtInRegistryHolder(), new EssenceValue(metal, 1), false);

        // Metals
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.IRON_NUGGET.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.RAW_IRON_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.RAW_IRON.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Tags.Items.INGOTS_IRON, new EssenceValue(metal, 1), false)
                .add(Items.IRON_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.GOLD_NUGGET.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.RAW_GOLD_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.RAW_GOLD.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Tags.Items.INGOTS_GOLD, new EssenceValue(metal, 1), false)
                .add(Items.GOLD_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.NETHERITE_SCRAP.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1), false)
                .add(Tags.Items.INGOTS_NETHERITE, new EssenceValue(metal, 1, nether, 1), false)
                .add(Items.NETHERITE_BLOCK.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1), false);

        // Gems
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.AMETHYST_CLUSTER.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.AMETHYST_SHARD.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.AMETHYST_BLOCK.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.SMALL_AMETHYST_BUD.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.MEDIUM_AMETHYST_BUD.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.LARGE_AMETHYST_BUD.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.DIAMOND.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.DIAMOND_BLOCK.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.EMERALD.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.EMERALD_BLOCK.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.LAPIS_LAZULI.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.LAPIS_BLOCK.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.QUARTZ.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.QUARTZ_BLOCK.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.CHISELED_QUARTZ_BLOCK.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1),
                        false)
                .add(Items.QUARTZ_BRICKS.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.QUARTZ_SLAB.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.QUARTZ_PILLAR.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.QUARTZ_STAIRS.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.SMOOTH_QUARTZ.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.SMOOTH_QUARTZ_SLAB.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.SMOOTH_QUARTZ_STAIRS.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false)
                .add(Items.ECHO_SHARD.builtInRegistryHolder(), new EssenceValue(crystal, 1, dark, 1), false)

                .add(Items.PRISMARINE_SHARD.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.PRISMARINE.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.PRISMARINE_CRYSTALS.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.DARK_PRISMARINE.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.DARK_PRISMARINE_SLAB.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.DARK_PRISMARINE_STAIRS.builtInRegistryHolder(),
                        new EssenceValue(crystal, 1, water, 1, order, 1), false)
                .add(Items.DARK_PRISMARINE.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.PRISMARINE_SLAB.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1), false)
                .add(Items.PRISMARINE_BRICK_SLAB.builtInRegistryHolder(),
                        new EssenceValue(crystal, 1, water, 1, order, 1), false)
                .add(Items.PRISMARINE_BRICKS.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1, order, 1),
                        false)
                .add(Items.PRISMARINE_BRICK_STAIRS.builtInRegistryHolder(),
                        new EssenceValue(crystal, 1, water, 1, order, 1), false)
                .add(Items.PRISMARINE_STAIRS.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1, order, 1),
                        false)
                .add(Items.PRISMARINE_WALL.builtInRegistryHolder(), new EssenceValue(crystal, 1, water, 1, order, 1),
                        false)
                .add(Items.NETHER_STAR.builtInRegistryHolder(), new EssenceValue(crystal, 1, nether, 1), false);

        // Stone Structues
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.FENCES_NETHER_BRICK, new EssenceValue(nether, 1, earth, 1), false)
                .add(ItemTags.STONE_BUTTONS, new EssenceValue(earth, 1), false)
                .add(Items.STONE_PRESSURE_PLATE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.LIGHT_WEIGHTED_PRESSURE_PLATE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.HEAVY_WEIGHTED_PRESSURE_PLATE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE.builtInRegistryHolder(), new EssenceValue(earth, 1),
                        false);

        // Wooden Structures
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.FENCE_GATES_WOODEN, new EssenceValue(plant, 1, order, 1), false)
                .add(Items.PALE_OAK_FENCE_GATE.builtInRegistryHolder(), new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.SIGNS, new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.HANGING_SIGNS, new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.WOODEN_BUTTONS, new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.WOODEN_DOORS, new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.WOODEN_FENCES, new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.WOODEN_PRESSURE_PLATES, new EssenceValue(plant, 1, order, 1), false)
                .add(ItemTags.WOODEN_TRAPDOORS, new EssenceValue(plant, 1, order, 1), false)
                .add(Tags.Items.BARRELS_WOODEN, new EssenceValue(plant, 1, order, 1), false)
                .add(Tags.Items.CHESTS_WOODEN, new EssenceValue(plant, 1, order, 1), false)
                .add(Items.LECTERN.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.LADDER.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.SCAFFOLDING.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.TARGET.builtInRegistryHolder(), new EssenceValue(plant, 1), false);

        // Metal Structures
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.EXPOSED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WEATHERED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.OXIDIZED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.EXPOSED_COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WEATHERED_COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.OXIDIZED_COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.EXPOSED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WEATHERED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.OXIDIZED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WAXED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WAXED_EXPOSED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_WEATHERED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_OXIDIZED_COPPER_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WAXED_EXPOSED_COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_WEATHERED_COPPER_TRAPDOOR.builtInRegistryHolder(),
                        new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.WAXED_EXPOSED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_WEATHERED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)
                .add(Items.WAXED_OXIDIZED_COPPER_GRATE.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1),
                        false)

                .add(Items.IRON_DOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.IRON_TRAPDOOR.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.RAIL.builtInRegistryHolder(), new EssenceValue(metal, 1, order, 1), false)
                .add(Items.IRON_BARS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CHAIN.builtInRegistryHolder(), new EssenceValue(metal, 1), false);

        // Redstone
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.REDSTONE.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.REDSTONE_BLOCK.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.ACTIVATOR_RAIL.builtInRegistryHolder(), new EssenceValue(power, 1, order, 1), false)
                .add(Items.DETECTOR_RAIL.builtInRegistryHolder(), new EssenceValue(power, 1, order, 1), false)
                .add(Items.POWERED_RAIL.builtInRegistryHolder(), new EssenceValue(power, 1, order, 1), false)
                .add(Items.NOTE_BLOCK.builtInRegistryHolder(), new EssenceValue(power, 1, order, 1), false);

        // Knowledge
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.POTIONS, new EssenceValue(water, 1, knowledge, 1), false)
                .add(Items.BOOKSHELF.builtInRegistryHolder(), new EssenceValue(order, 1, knowledge, 1), false)
                .add(Items.CHISELED_BOOKSHELF.builtInRegistryHolder(), new EssenceValue(order, 1, knowledge, 1), false)
                .add(Items.ENCHANTING_TABLE.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.ENCHANTED_GOLDEN_APPLE.builtInRegistryHolder(), new EssenceValue(plant, 1, knowledge, 1),
                        false)
                .add(Items.BEACON.builtInRegistryHolder(), new EssenceValue(knowledge, 1, crystal, 1), false)
                .add(Items.CONDUIT.builtInRegistryHolder(), new EssenceValue(knowledge, 1, water, 1), false)
                .add(Items.HEART_OF_THE_SEA.builtInRegistryHolder(), new EssenceValue(water, 1, crystal, 1), false)
                .add(Items.END_CRYSTAL.builtInRegistryHolder(), new EssenceValue(end, 1, crystal, 1), false)

                .add(Items.PAPER.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.BOOK.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.WRITABLE_BOOK.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.WRITTEN_BOOK.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.ENCHANTED_BOOK.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.MAP.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.FILLED_MAP.builtInRegistryHolder(), new EssenceValue(knowledge, 1, space, 1), false)
                .add(Items.EXPERIENCE_BOTTLE.builtInRegistryHolder(), new EssenceValue(knowledge, 1), false)
                .add(Items.FIRE_CHARGE.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.WIND_CHARGE.builtInRegistryHolder(), new EssenceValue(air, 1), false)
                .add(Items.DRAGON_BREATH.builtInRegistryHolder(), new EssenceValue(end, 1), false)

                .add(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(),
                        new EssenceValue(knowledge, 1), false)
                .add(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false)
                .add(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE.builtInRegistryHolder(), new EssenceValue(knowledge, 1),
                        false);

        // Light sources
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.CANDLES, new EssenceValue(light, 1, animal, 1), false)
                .add(Items.TORCH.builtInRegistryHolder(), new EssenceValue(fire, 1, light, 1), false)
                .add(Items.SOUL_TORCH.builtInRegistryHolder(), new EssenceValue(nether, 1, light, 1), false)
                .add(Items.CAMPFIRE.builtInRegistryHolder(), new EssenceValue(fire, 1, light, 1), false)
                .add(Items.SOUL_CAMPFIRE.builtInRegistryHolder(), new EssenceValue(nether, 1, light, 1), false)
                .add(Items.LANTERN.builtInRegistryHolder(), new EssenceValue(light, 1, metal, 1), false)
                .add(Items.SOUL_LANTERN.builtInRegistryHolder(), new EssenceValue(light, 1, nether, 1), false)
                .add(Items.REDSTONE_TORCH.builtInRegistryHolder(), new EssenceValue(power, 1, light, 1), false)
                .add(Items.REDSTONE_LAMP.builtInRegistryHolder(), new EssenceValue(power, 1, light, 1), false)
                .add(Items.COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1), false)
                .add(Items.SHROOMLIGHT.builtInRegistryHolder(), new EssenceValue(light, 1, life, 1), false)
                .add(Items.EXPOSED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1), false)
                .add(Items.WEATHERED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1), false)
                .add(Items.OXIDIZED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1), false)
                .add(Items.WAXED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1), false)
                .add(Items.WAXED_EXPOSED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1),
                        false)
                .add(Items.WAXED_WEATHERED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1),
                        false)
                .add(Items.WAXED_OXIDIZED_COPPER_BULB.builtInRegistryHolder(), new EssenceValue(metal, 1, light, 1),
                        false)
                .add(Items.GLOWSTONE_DUST.builtInRegistryHolder(), new EssenceValue(nether, 1, light, 1), false)
                .add(Items.GLOWSTONE.builtInRegistryHolder(), new EssenceValue(nether, 1, light, 1), false)
                .add(Items.JACK_O_LANTERN.builtInRegistryHolder(), new EssenceValue(light, 1, plant, 1), false)
                .add(Items.SEA_LANTERN.builtInRegistryHolder(), new EssenceValue(light, 1, water, 1), false)
                .add(Items.OCHRE_FROGLIGHT.builtInRegistryHolder(), new EssenceValue(light, 1, water, 1, life, 1),
                        false)
                .add(Items.VERDANT_FROGLIGHT.builtInRegistryHolder(), new EssenceValue(light, 1, water, 1, life, 1),
                        false)
                .add(Items.PEARLESCENT_FROGLIGHT.builtInRegistryHolder(), new EssenceValue(light, 1, water, 1, life, 1),
                        false);

        // Archaeology
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(ItemTags.DECORATED_POT_SHERDS, new EssenceValue(earth, 1, order, 1), false)
                .add(Items.DECORATED_POT.builtInRegistryHolder(), new EssenceValue(earth, 1, order, 1), false);

        // Equipment
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.BUCKET.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.LAVA_BUCKET.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Tags.Items.TOOLS_SHIELD, new EssenceValue(chaos, 1), false)
                .add(Items.HEAVY_CORE.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.MACE.builtInRegistryHolder(), new EssenceValue(metal, 1, chaos, 1), false)
                .add(Items.ELYTRA.builtInRegistryHolder(), new EssenceValue(air, 1), false)
                .add(Items.TURTLE_HELMET.builtInRegistryHolder(), new EssenceValue(water, 1), false)
                .add(Items.CROSSBOW.builtInRegistryHolder(), new EssenceValue(air, 1, chaos, 1), false)
                .add(Items.BOW.builtInRegistryHolder(), new EssenceValue(air, 1, chaos, 1), false)
                .add(Items.ARROW.builtInRegistryHolder(), new EssenceValue(air, 1, chaos, 1), false)
                .add(Items.SPECTRAL_ARROW.builtInRegistryHolder(), new EssenceValue(death, 1, chaos, 1), false)
                .add(Items.TIPPED_ARROW.builtInRegistryHolder(), new EssenceValue(chaos, 1), false)
                .add(Items.WOODEN_SWORD.builtInRegistryHolder(), new EssenceValue(plant, 1, chaos, 1), false)
                .add(Items.STONE_SWORD.builtInRegistryHolder(), new EssenceValue(earth, 1, chaos, 1), false)
                .add(Items.IRON_SWORD.builtInRegistryHolder(), new EssenceValue(metal, 1, chaos, 1), false)
                .add(Items.GOLDEN_SWORD.builtInRegistryHolder(), new EssenceValue(metal, 1, chaos, 1), false)
                .add(Items.DIAMOND_SWORD.builtInRegistryHolder(), new EssenceValue(crystal, 1, chaos, 1), false)
                .add(Items.NETHERITE_SWORD.builtInRegistryHolder(), new EssenceValue(nether, 1, metal, 1, chaos, 1),
                        false)
                .add(Items.WOODEN_AXE.builtInRegistryHolder(), new EssenceValue(plant, 1, chaos, 1), false)
                .add(Items.STONE_AXE.builtInRegistryHolder(), new EssenceValue(earth, 1, chaos, 1), false)
                .add(Items.IRON_AXE.builtInRegistryHolder(), new EssenceValue(metal, 1, chaos, 1), false)
                .add(Items.GOLDEN_AXE.builtInRegistryHolder(), new EssenceValue(metal, 1, chaos, 1), false)
                .add(Items.DIAMOND_AXE.builtInRegistryHolder(), new EssenceValue(crystal, 1, chaos, 1), false)
                .add(Items.NETHERITE_AXE.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1, chaos, 1),
                        false)
                .add(Items.WOODEN_SHOVEL.builtInRegistryHolder(), new EssenceValue(plant, 1, earth, 1), false)
                .add(Items.STONE_SHOVEL.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.IRON_SHOVEL.builtInRegistryHolder(), new EssenceValue(metal, 1, earth, 1), false)
                .add(Items.GOLDEN_SHOVEL.builtInRegistryHolder(), new EssenceValue(metal, 1, earth, 1), false)
                .add(Items.DIAMOND_SHOVEL.builtInRegistryHolder(), new EssenceValue(crystal, 1, earth, 1), false)
                .add(Items.NETHERITE_SHOVEL.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1, earth, 1),
                        false)
                .add(Items.WOODEN_HOE.builtInRegistryHolder(), new EssenceValue(plant, 1, life, 1), false)
                .add(Items.STONE_HOE.builtInRegistryHolder(), new EssenceValue(earth, 1, life, 1), false)
                .add(Items.IRON_HOE.builtInRegistryHolder(), new EssenceValue(metal, 1, life, 1), false)
                .add(Items.GOLDEN_HOE.builtInRegistryHolder(), new EssenceValue(metal, 1, life, 1), false)
                .add(Items.DIAMOND_HOE.builtInRegistryHolder(), new EssenceValue(crystal, 1, life, 1), false)
                .add(Items.NETHERITE_HOE.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1, life, 1), false)
                .add(Items.WOODEN_PICKAXE.builtInRegistryHolder(), new EssenceValue(plant, 1, earth, 1), false)
                .add(Items.STONE_PICKAXE.builtInRegistryHolder(), new EssenceValue(earth, 1), false)
                .add(Items.IRON_PICKAXE.builtInRegistryHolder(), new EssenceValue(metal, 1, earth, 1), false)
                .add(Items.GOLDEN_PICKAXE.builtInRegistryHolder(), new EssenceValue(metal, 1, earth, 1), false)
                .add(Items.DIAMOND_PICKAXE.builtInRegistryHolder(), new EssenceValue(crystal, 1, earth, 1), false)
                .add(Items.NETHERITE_PICKAXE.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1, earth, 1),
                        false)

                .add(Items.LEATHER_HELMET.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.LEATHER_CHESTPLATE.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.LEATHER_LEGGINGS.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.LEATHER_BOOTS.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.CHAINMAIL_HELMET.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CHAINMAIL_CHESTPLATE.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CHAINMAIL_LEGGINGS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.CHAINMAIL_BOOTS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.IRON_HELMET.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.IRON_CHESTPLATE.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.IRON_LEGGINGS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.IRON_BOOTS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.GOLDEN_HELMET.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.GOLDEN_CHESTPLATE.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.GOLDEN_LEGGINGS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.GOLDEN_BOOTS.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.DIAMOND_HELMET.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.DIAMOND_CHESTPLATE.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.DIAMOND_LEGGINGS.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.DIAMOND_BOOTS.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.NETHERITE_HELMET.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1), false)
                .add(Items.NETHERITE_CHESTPLATE.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1), false)
                .add(Items.NETHERITE_LEGGINGS.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1), false)
                .add(Items.NETHERITE_BOOTS.builtInRegistryHolder(), new EssenceValue(metal, 1, nether, 1), false)

                .add(Items.LEATHER_HORSE_ARMOR.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.IRON_HORSE_ARMOR.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.GOLDEN_HORSE_ARMOR.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.DIAMOND_HORSE_ARMOR.builtInRegistryHolder(), new EssenceValue(crystal, 1), false)
                .add(Items.TRIDENT.builtInRegistryHolder(), new EssenceValue(water, 1, chaos, 1), false);

        // Other
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Items.BRUSH.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.TRIAL_KEY.builtInRegistryHolder(), new EssenceValue(chaos, 1), false)
                .add(Items.OMINOUS_TRIAL_KEY.builtInRegistryHolder(), new EssenceValue(chaos, 1), false)
                .add(Items.OMINOUS_BOTTLE.builtInRegistryHolder(), new EssenceValue(water, 1, chaos, 1), false)
                .add(Items.FISHING_ROD.builtInRegistryHolder(), new EssenceValue(order, 1, plant, 1), false)
                .add(Items.LEAD.builtInRegistryHolder(), new EssenceValue(order, 1, animal, 1), false)
                .add(Items.NAME_TAG.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.SHEARS.builtInRegistryHolder(), new EssenceValue(order, 1, metal, 1), false)
                .add(Items.CARROT_ON_A_STICK.builtInRegistryHolder(), new EssenceValue(plant, 1), false)
                .add(Items.SADDLE.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(Items.WARPED_FUNGUS_ON_A_STICK.builtInRegistryHolder(), new EssenceValue(plant, 1, nether, 1),
                        false)
                .add(Items.WOLF_ARMOR.builtInRegistryHolder(), new EssenceValue(animal, 1), false)
                .add(ItemTags.BUNDLES, new EssenceValue(animal, 1, order, 1), false)
                .add(ItemTags.SHULKER_BOXES, new EssenceValue(end, 1, order, 1), false)
                .add(Tags.Items.CHESTS_ENDER, new EssenceValue(space, 1, order, 1), false)
                .add(Tags.Items.MUSIC_DISCS, new EssenceValue(order, 1, knowledge, 1), false)
                .add(Items.DISC_FRAGMENT_5.builtInRegistryHolder(), new EssenceValue(order, 1, knowledge, 1), false)
                .add(Items.JUKEBOX.builtInRegistryHolder(), new EssenceValue(order, 1, power, 1), false)
                .add(Items.END_ROD.builtInRegistryHolder(), new EssenceValue(end, 1), false)

                .add(Items.HOPPER.builtInRegistryHolder(), new EssenceValue(metal, 1), false)
                .add(Items.LEVER.builtInRegistryHolder(), new EssenceValue(earth, 1, plant, 1), false)
                .add(Items.LIGHTNING_ROD.builtInRegistryHolder(), new EssenceValue(metal, 1, air, 1), false)
                .add(Items.TRIPWIRE_HOOK.builtInRegistryHolder(), new EssenceValue(metal, 1, plant, 1), false)

                .add(Items.GUNPOWDER.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.TNT.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.FLINT_AND_STEEL.builtInRegistryHolder(), new EssenceValue(fire, 1, metal, 1), false)
                .add(Items.BOWL.builtInRegistryHolder(), new EssenceValue(plant, 1), false)

                .add(Items.REPEATER.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.COMPARATOR.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.PISTON.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.STICKY_PISTON.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.OBSERVER.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.DISPENSER.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.DROPPER.builtInRegistryHolder(), new EssenceValue(power, 1), false)
                .add(Items.DAYLIGHT_DETECTOR.builtInRegistryHolder(), new EssenceValue(power, 1), false)

                .add(Items.ITEM_FRAME.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.GLOW_ITEM_FRAME.builtInRegistryHolder(), new EssenceValue(order, 1, light, 1), false)
                .add(Items.FLOWER_POT.builtInRegistryHolder(), new EssenceValue(order, 1, plant, 1), false)
                .add(Items.PAINTING.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.COMPASS.builtInRegistryHolder(), new EssenceValue(space, 1, power, 1), false)
                .add(Items.LODESTONE.builtInRegistryHolder(), new EssenceValue(space, 1), false)
                .add(Items.RECOVERY_COMPASS.builtInRegistryHolder(), new EssenceValue(space, 1, death, 1), false)
                .add(Items.RESPAWN_ANCHOR.builtInRegistryHolder(), new EssenceValue(nether, 1, death, 1), false)
                .add(Items.CLOCK.builtInRegistryHolder(), new EssenceValue(time, 1), false)
                .add(Items.SPYGLASS.builtInRegistryHolder(), new EssenceValue(space, 1), false)

                .add(Items.FIREWORK_ROCKET.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.FIREWORK_STAR.builtInRegistryHolder(), new EssenceValue(fire, 1), false)
                .add(Items.ARMOR_STAND.builtInRegistryHolder(), new EssenceValue(order, 1), false)
                .add(Items.TOTEM_OF_UNDYING.builtInRegistryHolder(), new EssenceValue(life, 1, death, 1), false)
                .add(Items.GOAT_HORN.builtInRegistryHolder(), new EssenceValue(animal, 1, order, 1), false)
                .add(Items.BELL.builtInRegistryHolder(), new EssenceValue(metal, 1), false)

                .build();
    }
}
