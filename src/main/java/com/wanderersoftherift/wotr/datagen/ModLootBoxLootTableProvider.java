package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModTags;
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

public record ModLootBoxLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {
    private static final Map<RunegemTier, TagKey<RunegemData>> GEODE_RUNEGEM_TAGS = Map.of(
            RunegemTier.RAW, ModTags.Runegems.GEODE_RAW, RunegemTier.CUT, ModTags.Runegems.GEODE_CUT,
            RunegemTier.SHAPED, ModTags.Runegems.GEODE_SHAPED, RunegemTier.POLISHED, ModTags.Runegems.GEODE_POLISHED,
            RunegemTier.FRAMED, ModTags.Runegems.GEODE_FRAMED, RunegemTier.UNIQUE, ModTags.Runegems.GEODE_UNIQUE
    );
    private static final Map<RunegemTier, TagKey<RunegemData>> MONSTER_RUNEGEM_TAGS = Map.of(
            RunegemTier.RAW, ModTags.Runegems.MONSTER_RAW, RunegemTier.CUT, ModTags.Runegems.MONSTER_CUT,
            RunegemTier.SHAPED, ModTags.Runegems.MONSTER_SHAPED, RunegemTier.POLISHED,
            ModTags.Runegems.MONSTER_POLISHED, RunegemTier.FRAMED, ModTags.Runegems.MONSTER_FRAMED, RunegemTier.UNIQUE,
            ModTags.Runegems.MONSTER_UNIQUE
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
        HolderLookup.RegistryLookup<RunegemData> reg = registries.lookupOrThrow(ModDatapackRegistries.RUNEGEM_DATA_KEY);
        consumer.accept(getResourceKey("loot_box/" + tier.getName() + "_runegem_" + suffix),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.RUNEGEM)
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
