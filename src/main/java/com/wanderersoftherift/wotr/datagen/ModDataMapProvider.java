package com.wanderersoftherift.wotr.datagen;

import com.google.common.collect.ImmutableMap;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataMaps;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
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

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        ResourceLocation meat = WanderersOfTheRift.id("meat");
        ResourceLocation earth = WanderersOfTheRift.id("earth");
        ResourceLocation life = WanderersOfTheRift.id("life");
        // Core
        this.builder(ModDataMaps.ESSENCE_VALUE_DATA)
                .add(Tags.Items.FOODS_RAW_MEAT, new EssenceValue(meat, 1), false)
                .add(Tags.Items.FOODS_COOKED_MEAT, new EssenceValue(meat, 2), false)
                .add(Tags.Items.COBBLESTONES, new EssenceValue(earth, 1), false)
                .add(Tags.Items.STONES, new EssenceValue(earth, 2), false)
                .add(Tags.Items.STRIPPED_LOGS, new EssenceValue(life, 8), false)
                .add(Tags.Items.CROPS, new EssenceValue(life, 1), false)
                .add(Items.POTATO.builtInRegistryHolder(), new EssenceValue(ImmutableMap.<ResourceLocation, Integer>builder().put(life, 2).put(earth, 1).build()), false)
                .build();


    }
}
