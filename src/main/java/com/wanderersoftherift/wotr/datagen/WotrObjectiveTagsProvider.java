package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

public class WotrObjectiveTagsProvider extends TagsProvider<ObjectiveType> {
    // Get parameters from the `GatherDataEvent`s.
    public WotrObjectiveTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, WotrRegistries.Keys.OBJECTIVES, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(WotrTags.Objectives.RANDOM_SELECTABLE).add(TagEntry.optionalElement(WanderersOfTheRift.id("kill")));
    }
}
