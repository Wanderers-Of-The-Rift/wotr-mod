package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;

public class ModAbilityTagsProvider extends TagsProvider<AbstractAbility> {
    // Get parameters from the `GatherDataEvent`s.
    public ModAbilityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, RegistryEvents.ABILITY_REGISTRY, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ModTags.Abilities.RIFT_DROPS)
                .add(ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("dash")))
                .add(ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("fireball")))
                .add(ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("heal")))
                .add(ResourceKey.create(RegistryEvents.ABILITY_REGISTRY, WanderersOfTheRift.id("icicles")));
    }
}
