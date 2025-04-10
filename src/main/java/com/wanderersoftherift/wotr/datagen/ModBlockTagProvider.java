package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BlockTags.*;

/**
 *  Handles Data Generation for Block Tags of the Wotr mod
 */
public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(family -> {
            if(family.getVariant(BlockFamily.Variant.STAIRS) != null) {
                tag(STAIRS).add(family.getVariant(BlockFamily.Variant.STAIRS).get());
            }
            if(family.getVariant(BlockFamily.Variant.SLAB) != null) {
                tag(SLABS).add(family.getVariant(BlockFamily.Variant.SLAB).get());
            }
            if(family.getVariant(BlockFamily.Variant.WALL) != null) {
                tag(WALLS).add(family.getVariant(BlockFamily.Variant.WALL).get());
            }
            if(family.getVariant(BlockFamily.Variant.FENCE) != null) {
                tag(FENCES).add(family.getVariant(BlockFamily.Variant.FENCE).get());
            }
            if(family.getVariant(BlockFamily.Variant.FENCE_GATE) != null) {
                tag(FENCE_GATES).add(family.getVariant(BlockFamily.Variant.FENCE_GATE).get());
            }
        });
    }

}
