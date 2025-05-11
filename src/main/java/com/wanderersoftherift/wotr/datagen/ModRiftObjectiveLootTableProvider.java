package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public record ModRiftObjectiveLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift_objective/success"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1, 3))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .setWeight(40)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                        .add(LootItem.lootTableItem(ModItems.SKILL_THREAD)
                                .setWeight(30)
                                .apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(16, 0.8f))))
                        .add(NestedLootTable.lootTableReference(getResourceKey("rift/runegem")).setWeight(20))
                        .add(LootItem.lootTableItem(Items.NETHERITE_INGOT).setWeight(20))));
        consumer.accept(getResourceKey("rift_objective/fail"), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.PAPER)
                                        .apply(SetNameFunction.setName(
                                                Component.translatable(
                                                        WanderersOfTheRift.translationId("itemname", "consolation1")),
                                                SetNameFunction.Target.ITEM_NAME)))
                                .add(LootItem.lootTableItem(Items.PAPER)
                                        .apply(SetNameFunction.setName(
                                                Component.translatable(
                                                        WanderersOfTheRift.translationId("itemname", "consolation2")),
                                                SetNameFunction.Target.ITEM_NAME)))
                                .add(LootItem.lootTableItem(Items.PAPER)
                                        .apply(SetNameFunction.setName(
                                                Component.translatable(
                                                        WanderersOfTheRift.translationId("itemname", "consolation3")),
                                                SetNameFunction.Target.ITEM_NAME)))
                                .add(LootItem.lootTableItem(Items.PAPER)
                                        .apply(SetNameFunction.setName(
                                                Component.translatable(
                                                        WanderersOfTheRift.translationId("itemname", "consolation4")),
                                                SetNameFunction.Target.ITEM_NAME)))
                )

        );
        consumer.accept(getResourceKey("rift_objective/survived"),
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(0))));
    }

    private static @NotNull ResourceKey<LootTable> getResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, path));
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

}