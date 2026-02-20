package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.TossItemGoal;
import com.wanderersoftherift.wotr.util.RandomUtil;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Goal provider that generates TossItem goals from a loot table, by combining the results of a number of rolls and then
 * picking items from the result
 * 
 * @param lootTable The loot table to generate from
 * @param lootRolls The number of times to roll on the loot table
 * @param itemTypes The number of distinct items to create goals for
 */
public record TossFromLoottableGoalProvider(ResourceKey<LootTable> lootTable, NumberProvider lootRolls,
        NumberProvider itemTypes) implements GoalProvider {

    public static final MapCodec<TossFromLoottableGoalProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.LOOT_TABLE)
                            .fieldOf("loot_table")
                            .forGetter(TossFromLoottableGoalProvider::lootTable),
                    NumberProviders.CODEC.fieldOf("loot_rolls").forGetter(TossFromLoottableGoalProvider::lootRolls),
                    NumberProviders.CODEC.optionalFieldOf("item_types", ConstantValue.exactly(1))
                            .forGetter(TossFromLoottableGoalProvider::itemTypes)
            ).apply(instance, TossFromLoottableGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootContext context) {
        LootTable table = context.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);

        int rolls = lootRolls.getInt(context);
        Object2IntMap<Item> itemCounts = new Object2IntArrayMap<>();
        for (int i = 0; i < rolls; i++) {
            table.getRandomItems(context, stack -> itemCounts.merge(stack.getItem(), stack.getCount(), Integer::sum));
        }
        return RandomUtil.randomSubset(itemCounts.keySet(), itemTypes.getInt(context), context.getRandom())
                .stream()
                .map(itemType -> new TossItemGoal(Ingredient.of(itemType), itemCounts.getInt(itemType)))
                .collect(Collectors.toList());
    }
}
