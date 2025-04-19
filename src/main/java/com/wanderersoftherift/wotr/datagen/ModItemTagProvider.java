package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

/* Handles Data Generation for Block Tags of the Wotr mod */
public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.SOCKETABLE).addTag(Tags.Items.ARMORS).addTag(Tags.Items.TOOLS);
    }
}