package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModRunegemDataTagsProvider extends TagsProvider<RunegemData> {
    public ModRunegemDataTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, ModDatapackRegistries.RUNEGEM_DATA_KEY, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tierTag(RunegemTier.RAW, ModTags.Runegems.RAW);
        tierTag(RunegemTier.CUT, ModTags.Runegems.CUT);
        tierTag(RunegemTier.SHAPED, ModTags.Runegems.SHAPED);
        tierTag(RunegemTier.POLISHED, ModTags.Runegems.POLISHED);
        tierTag(RunegemTier.FRAMED, ModTags.Runegems.FRAMED);
        tierTag(RunegemTier.UNIQUE, ModTags.Runegems.UNIQUE);

        geodeTag(RunegemTier.RAW, ModTags.Runegems.GEODE_RAW);
        geodeTag(RunegemTier.CUT, ModTags.Runegems.GEODE_CUT);
        geodeTag(RunegemTier.SHAPED, ModTags.Runegems.GEODE_SHAPED);
        geodeTag(RunegemTier.POLISHED, ModTags.Runegems.GEODE_POLISHED);
        geodeTag(RunegemTier.FRAMED, ModTags.Runegems.GEODE_FRAMED);
    }

    private void geodeTag(RunegemTier tier, TagKey<RunegemData> geodeTag) {
        ModRuneGemDataProvider.DATA.forEach((key, value) -> {
            if (value.tier() == tier) {
                if (key.location().getPath().contains("zombie") && !key.location().getPath().contains("skeleton")
                        && !key.location().getPath().contains("creeper")) {
                    tag(geodeTag).add(key);
                }
            }
        });
    }

    private void tierTag(RunegemTier tier, TagKey<RunegemData> tierTag) {
        ModRuneGemDataProvider.DATA.forEach((key, value) -> {
            if (value.tier() == tier) {
                tag(tierTag).add(key);
            }
        });
    }
}
