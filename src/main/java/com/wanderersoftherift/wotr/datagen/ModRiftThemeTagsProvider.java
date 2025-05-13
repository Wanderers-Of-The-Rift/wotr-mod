package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModRiftThemes;
import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.world.level.levelgen.theme.RiftTheme;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagEntry;

import java.util.concurrent.CompletableFuture;

public class ModRiftThemeTagsProvider extends TagsProvider<RiftTheme> {
    // Get parameters from the `GatherDataEvent`s.
    public ModRiftThemeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, ModRiftThemes.RIFT_THEME_KEY, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(ModTags.RiftThemes.RANDOM_SELECTABLE).add(TagEntry.optionalElement(WanderersOfTheRift.id("cave")))
                .add(TagEntry.optionalElement(WanderersOfTheRift.id("forest")));
    }
}
