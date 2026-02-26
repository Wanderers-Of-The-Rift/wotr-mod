package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public record WotrAnomalyRewardLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {

        consumer.accept(getKey("theme/buzzy_bees"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.HONEY_BOTTLE)
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(1, 0.8f))))
                                .add(LootItem.lootTableItem(Items.HONEYCOMB)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.8f))))
                ));

        consumer.accept(getKey("theme/desert"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(2, 0.75f))))
                                .add(LootItem.lootTableItem(Items.DEAD_BUSH)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(5, 0.8f))))
                ));

        consumer.accept(getKey("theme/forest"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.EMERALD))
                                .when(LootItemRandomChanceCondition.randomChance(0.2f))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.OAK_LOG)
                                        .setWeight(50)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(2, 0.8f))))
                                .add(LootItem.lootTableItem(Items.BIRCH_LOG)
                                        .setWeight(50)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(2, 0.8f))))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.OAK_SAPLING)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.8f))))
                                .add(LootItem.lootTableItem(Items.BIRCH_SAPLING)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.8f))))
                ));

        consumer.accept(getKey("theme/jungle"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.COCOA_BEANS)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.8f))))
                                .add(LootItem.lootTableItem(Items.VINE)
                                        .setWeight(30)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.7f))))
                                .add(LootItem.lootTableItem(Items.JUNGLE_SAPLING)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.8f))))
                                .add(LootItem.lootTableItem(Items.JUNGLE_LOG)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(5, 0.8f))))
                ));

        consumer.accept(getKey("theme/cave"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                        .setWeight(30)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.7f))))
                                .add(LootItem.lootTableItem(Items.COAL)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.8f))))
                                .add(LootItem.lootTableItem(Items.TORCH)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(16, 0.8f))))
                ));

        consumer.accept(getKey("theme/deepfrost"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        basePool()
                                .add(LootItem.lootTableItem(Items.PACKED_ICE)
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.8f))))
                                .add(LootItem.lootTableItem(Items.ICE)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(8, 0.8f))))

                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.SNOWBALL)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(8, 0.85f))))
                                .add(LootItem.lootTableItem(Items.SNOW_BLOCK)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                ));

        consumer.accept(getKey("theme/nether"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.BLAZE_ROD)
                                        .setWeight(50)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.6f)))
                                        .when(LootItemRandomChanceCondition.randomChance(0.2f)))
                                .add(LootItem.lootTableItem(Items.GHAST_TEAR)
                                        .setWeight(50)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(1, 0.5f)))
                                        .when(LootItemRandomChanceCondition.randomChance(0.1f)))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.NETHER_WART)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.5f))))
                                .add(LootItem.lootTableItem(Items.GLOWSTONE)
                                        .setWeight(10)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.5f))))
                                .add(LootItem.lootTableItem(Items.QUARTZ)
                                        .setWeight(10)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.5f))))
                                .add(LootItem.lootTableItem(Items.SOUL_SAND)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(5, 0.5f))))
                ));

        consumer.accept(getKey("theme/swamp"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.SLIME_BALL)
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(2, 0.8f))))
                                .add(LootItem.lootTableItem(Items.LILY_PAD)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.8f))))
                                .add(LootItem.lootTableItem(Items.BLUE_ORCHID)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.8f))))
                ));

        consumer.accept(getKey("theme/mesa"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.RED_SAND)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(8, 0.9f))))
                                .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.8f))))
                                .add(LootItem.lootTableItem(Items.TERRACOTTA)
                                        .setWeight(35)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(7, 0.8f))))
                ));

        consumer.accept(getKey("theme/meadow"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.DANDELION)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.ALLIUM)
                                        .setWeight(25)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.7f))))
                ));

        consumer.accept(getKey("theme/mushroomcave"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Blocks.MYCELIUM)
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(
                                                ConstantValue.exactly(1)))
                                        .when(LootItemRandomChanceCondition.randomChance(0.2f))))
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.RED_MUSHROOM_BLOCK)
                                        .setWeight(25)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.7f))))
                                .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM_BLOCK)
                                        .setWeight(25)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.7f))))
                                .add(LootItem.lootTableItem(Items.MUSHROOM_STEM)
                                        .setWeight(25)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.7f))))
                )
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.RED_MUSHROOM)
                                        .setWeight(50)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                        .setWeight(50)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                )
        );

        consumer.accept(getKey("theme/coral"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.BRAIN_CORAL)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.BUBBLE_CORAL)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.TUBE_CORAL)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.FIRE_CORAL)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.HORN_CORAL)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.SEA_PICKLE)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                                .add(LootItem.lootTableItem(Items.SEAGRASS)
                                        .setWeight(14)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                ));

        consumer.accept(getKey("theme/noir"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.COAL_BLOCK)
                                        .setWeight(45)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(2, 0.85f))))
                                .add(LootItem.lootTableItem(Items.BLACK_DYE)
                                        .setWeight(45)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(4, 0.8f))))
                                .add(LootItem.lootTableItem(Items.GLOW_INK_SAC)
                                        .setWeight(10)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(2, 0.8f))))
                ));

        consumer.accept(getKey("theme/default"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.IRON_NUGGET)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(6, 0.85f))))
                ));

        consumer.accept(getKey("theme/color"), LootTable.lootTable()
                .withPool(basePool())
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.RED_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.ORANGE_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.YELLOW_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.LIME_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.GREEN_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.LIGHT_BLUE_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.CYAN_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.BLUE_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.PINK_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.MAGENTA_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))
                                .add(LootItem.lootTableItem(Items.PURPLE_DYE)
                                        .setWeight(9)
                                        .apply(SetItemCountFunction.setCount(
                                                BinomialDistributionGenerator.binomial(3, 0.85f))))

                ));
    }

    /**
     * Skill thread is included in all
     */
    private LootPool.Builder basePool() {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(WotrItems.SKILL_THREAD.get())
                        .apply(SetItemCountFunction.setCount(
                                BinomialDistributionGenerator.binomial(2, 0.75f)))
                        .when(LootItemRandomChanceCondition.randomChance(0.25f)));
    }

    private static @NotNull ResourceKey<LootTable> getKey(String path) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "anomaly_rewards/" + path)
        );
    }

    @Override
    public HolderLookup.Provider registries() {
        return this.registries;
    }
}
