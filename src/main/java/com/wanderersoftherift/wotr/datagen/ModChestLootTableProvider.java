package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public record ModChestLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("chests/wooden"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(3))
                                .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(20))
                                .add(LootItem.lootTableItem(Items.POTION)
                                        .setWeight(20)
                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                .add(LootItem.lootTableItem(Items.BREAD).setWeight(20))
                                .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(10))));
    }

    private static @NotNull ResourceKey<LootTable> getResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, path));
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

}
