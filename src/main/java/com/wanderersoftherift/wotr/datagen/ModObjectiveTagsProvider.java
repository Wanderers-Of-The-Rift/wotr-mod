package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

public class ModObjectiveTagsProvider extends TagsProvider<ObjectiveType> {
    // Get parameters from the `GatherDataEvent`s.
    public ModObjectiveTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, RegistryEvents.OBJECTIVE_REGISTRY, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ModTags.Objectives.RANDOM_SELECTABLE).add(TagEntry.optionalElement(WanderersOfTheRift.id("kill")));
    }
}
