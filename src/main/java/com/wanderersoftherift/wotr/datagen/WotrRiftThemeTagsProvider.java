package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

public class WotrRiftThemeTagsProvider extends TagsProvider<RiftTheme> {
    // Get parameters from the `GatherDataEvent`s.
    public WotrRiftThemeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, WotrRegistries.Keys.RIFT_THEMES, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(WotrTags.RiftThemes.RANDOM_SELECTABLE).add(TagEntry.optionalElement(WanderersOfTheRift.id("cave")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("buzzy_bees")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("desert")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("forest")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("mesa")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("mushroom")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("nether")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("noir")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("swamp")));
    }
}
