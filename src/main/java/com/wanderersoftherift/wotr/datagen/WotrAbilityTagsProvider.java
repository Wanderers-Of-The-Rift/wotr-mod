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
        tag(WotrTags.Abilities.ABILITY_DROPS_LOW)
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("dash")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("heal")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("firebolt")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("veinminer")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("teleport")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("stab_stab_slash")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("earth_dart")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("fire_dart")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("lightning_dart")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("ice_dart")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("recall")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("poison_dart")));
        tag(WotrTags.Abilities.ABILITY_DROPS_MEDIUM)
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("icicles")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("hook_shot")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("painful_sneak")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("life_steal")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("slime_wall")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("earth_breath")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("fire_breath")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("lightning_breath")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("ice_breath")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("poison_breath")));
        tag(WotrTags.Abilities.ABILITY_DROPS_HIGH)
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("fireball")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("group_hug")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("filth_aura")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("acid_splash")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("exploding_kittens")));
        // spotless:on
    }
}
