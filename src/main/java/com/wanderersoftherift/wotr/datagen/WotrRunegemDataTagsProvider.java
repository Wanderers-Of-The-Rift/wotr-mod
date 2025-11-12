package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class WotrRunegemDataTagsProvider extends TagsProvider<RunegemData> {
    public WotrRunegemDataTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        // Second parameter is the registry key we are generating the tags for.
        super(output, WotrRegistries.Keys.RUNEGEM_DATA, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tierTag(RunegemTier.RAW, WotrTags.Runegems.RAW);
        tierTag(RunegemTier.CUT, WotrTags.Runegems.CUT);
        tierTag(RunegemTier.SHAPED, WotrTags.Runegems.SHAPED);
        tierTag(RunegemTier.POLISHED, WotrTags.Runegems.POLISHED);
        tierTag(RunegemTier.FRAMED, WotrTags.Runegems.FRAMED);
        tierTag(RunegemTier.UNIQUE, WotrTags.Runegems.UNIQUE);

        geodeTag(RunegemTier.RAW, WotrTags.Runegems.GEODE_RAW);
        geodeTag(RunegemTier.CUT, WotrTags.Runegems.GEODE_CUT);
        geodeTag(RunegemTier.SHAPED, WotrTags.Runegems.GEODE_SHAPED);
        geodeTag(RunegemTier.POLISHED, WotrTags.Runegems.GEODE_POLISHED);
        geodeTag(RunegemTier.FRAMED, WotrTags.Runegems.GEODE_FRAMED);

        monsterTag(RunegemTier.RAW, WotrTags.Runegems.MONSTER_RAW);
        monsterTag(RunegemTier.CUT, WotrTags.Runegems.MONSTER_CUT);
        monsterTag(RunegemTier.SHAPED, WotrTags.Runegems.MONSTER_SHAPED);
        monsterTag(RunegemTier.POLISHED, WotrTags.Runegems.MONSTER_POLISHED);
        monsterTag(RunegemTier.FRAMED, WotrTags.Runegems.MONSTER_FRAMED);
    }

    private void geodeTag(RunegemTier tier, TagKey<RunegemData> geodeTag) {
        WotrRuneGemDataProvider.DATA.forEach((key, value) -> {
            if (value.tier() == tier) {
                if (!key.location().getPath().contains("zombie") && !key.location().getPath().contains("skeleton")
                        && !key.location().getPath().contains("creeper")
                        && !key.location().getPath().contains("enderman")) {
                    tag(geodeTag).add(key);
                }
            }
        });
    }

    private void monsterTag(RunegemTier tier, TagKey<RunegemData> tag) {
        WotrRuneGemDataProvider.DATA.forEach((key, value) -> {
            if (value.tier() == tier) {
                if (key.location().getPath().contains("zombie") || key.location().getPath().contains("skeleton")
                        || key.location().getPath().contains("creeper")
                        || key.location().getPath().contains("enderman")) {
                    tag(tag).add(key);
                }
            }
        });
    }

    private void tierTag(RunegemTier tier, TagKey<RunegemData> tierTag) {
        WotrRuneGemDataProvider.DATA.forEach((key, value) -> {
            if (value.tier() == tier) {
                tag(tierTag).add(key);
            }
        });
    }
}
