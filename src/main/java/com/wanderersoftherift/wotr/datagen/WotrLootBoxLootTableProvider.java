package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import com.wanderersoftherift.wotr.loot.functions.RunegemsFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;

public record WotrLootBoxLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
    private static final Map<RunegemTier, TagKey<RunegemData>> GEODE_RUNEGEM_TAGS = Map.of(
            RunegemTier.RAW, WotrTags.Runegems.GEODE_RAW, RunegemTier.CUT, WotrTags.Runegems.GEODE_CUT,
            RunegemTier.SHAPED, WotrTags.Runegems.GEODE_SHAPED, RunegemTier.POLISHED, WotrTags.Runegems.GEODE_POLISHED,
            RunegemTier.FRAMED, WotrTags.Runegems.GEODE_FRAMED, RunegemTier.UNIQUE, WotrTags.Runegems.GEODE_UNIQUE
    );
    private static final Map<RunegemTier, TagKey<RunegemData>> MONSTER_RUNEGEM_TAGS = Map.of(
            RunegemTier.RAW, WotrTags.Runegems.MONSTER_RAW, RunegemTier.CUT, WotrTags.Runegems.MONSTER_CUT,
            RunegemTier.SHAPED, WotrTags.Runegems.MONSTER_SHAPED, RunegemTier.POLISHED,
            WotrTags.Runegems.MONSTER_POLISHED, RunegemTier.FRAMED, WotrTags.Runegems.MONSTER_FRAMED,
            RunegemTier.UNIQUE, WotrTags.Runegems.MONSTER_UNIQUE
    );

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        generateRunegemTierTable(consumer, RunegemTier.RAW, GEODE_RUNEGEM_TAGS, "geode");
        generateRunegemTierTable(consumer, RunegemTier.SHAPED, GEODE_RUNEGEM_TAGS, "geode");
        generateRunegemTierTable(consumer, RunegemTier.CUT, GEODE_RUNEGEM_TAGS, "geode");
        generateRunegemTierTable(consumer, RunegemTier.POLISHED, GEODE_RUNEGEM_TAGS, "geode");
        generateRunegemTierTable(consumer, RunegemTier.FRAMED, GEODE_RUNEGEM_TAGS, "geode");
        generateRunegemTierTable(consumer, RunegemTier.RAW, MONSTER_RUNEGEM_TAGS, "monster");
        generateRunegemTierTable(consumer, RunegemTier.SHAPED, MONSTER_RUNEGEM_TAGS, "monster");
        generateRunegemTierTable(consumer, RunegemTier.CUT, MONSTER_RUNEGEM_TAGS, "monster");
        generateRunegemTierTable(consumer, RunegemTier.POLISHED, MONSTER_RUNEGEM_TAGS, "monster");
        generateRunegemTierTable(consumer, RunegemTier.FRAMED, MONSTER_RUNEGEM_TAGS, "monster");
    }

    private void generateRunegemTierTable(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer,
            RunegemTier tier,
            Map<RunegemTier, TagKey<RunegemData>> runegemTierTags,
            String suffix) {
        HolderLookup.RegistryLookup<RunegemData> reg = registries.lookupOrThrow(WotrRegistries.Keys.RUNEGEM_DATA);
        consumer.accept(getResourceKey("loot_box/" + tier.getName() + "_runegem_" + suffix),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(WotrItems.RUNEGEM)
                                        .setWeight(1)
                                        .apply(RunegemsFunction
                                                .setRunegemOptions(reg.getOrThrow(runegemTierTags.get(tier)))))));
    }

    private static @NotNull ResourceKey<LootTable> getResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, path));
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

}
