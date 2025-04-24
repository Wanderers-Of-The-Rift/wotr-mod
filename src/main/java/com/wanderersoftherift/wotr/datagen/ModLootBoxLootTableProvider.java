package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import com.wanderersoftherift.wotr.loot.functions.RunegemsFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public record ModLootBoxLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        generateRunegemGeodeTable(consumer, RunegemTier.RAW);
        generateRunegemGeodeTable(consumer, RunegemTier.SHAPED);
        generateRunegemGeodeTable(consumer, RunegemTier.CUT);
        generateRunegemGeodeTable(consumer, RunegemTier.POLISHED);
        generateRunegemGeodeTable(consumer, RunegemTier.FRAMED);
    }

    private static void generateRunegemGeodeTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer,
            RunegemTier tier) {
        consumer.accept(getResourceKey("loot_box/" + tier.getName() + "_runegem_geode"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.RUNEGEM)
                                        .setWeight(1)
                                        .apply(RunegemsFunction.setTier(tier)))));
    }

    private static @NotNull ResourceKey<LootTable> getResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, path));
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

}
