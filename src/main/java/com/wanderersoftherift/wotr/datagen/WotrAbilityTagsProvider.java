package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

public class WotrAbilityTagsProvider extends TagsProvider<Ability> {
    // Get parameters from the `GatherDataEvent`s.
    public WotrAbilityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, WotrRegistries.Keys.ABILITIES, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        // spotless:off
        tag(WotrTags.Abilities.RIFT_DROPS)
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("dash")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("fireball")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("heal")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("icicles")));
        // spotless:on
    }
}
