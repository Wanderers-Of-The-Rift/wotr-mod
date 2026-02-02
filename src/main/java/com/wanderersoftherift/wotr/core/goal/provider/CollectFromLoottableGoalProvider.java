package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.CollectItemGoal;
import com.wanderersoftherift.wotr.util.RandomUtil;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record CollectFromLoottableGoalProvider(ResourceKey<LootTable> lootTable, NumberProvider lootRolls,
        NumberProvider itemTypes) implements GoalProvider {

    public static final MapCodec<CollectFromLoottableGoalProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.LOOT_TABLE)
                            .fieldOf("loot_table")
                            .forGetter(CollectFromLoottableGoalProvider::lootTable),
                    NumberProviders.CODEC.fieldOf("loot_rolls").forGetter(CollectFromLoottableGoalProvider::lootRolls),
                    NumberProviders.CODEC.optionalFieldOf("item_types", ConstantValue.exactly(1))
                            .forGetter(CollectFromLoottableGoalProvider::itemTypes)
            ).apply(instance, CollectFromLoottableGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootContext context) {
        LootTable table = context.getLevel().getServer().reloadableRegistries().getLootTable(lootTable);
        List<ItemStack> output = new ArrayList<>();
        for (int i = 0; i < lootRolls.getInt(context); i++) {
            table.getRandomItems(context, output::add);
        }
        Object2IntMap<Item> itemCounts = new Object2IntArrayMap<>(output.size());
        for (ItemStack stack : output) {
            itemCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }
        return RandomUtil.randomSubset(itemCounts.keySet(), itemTypes.getInt(context), context.getRandom())
                .stream()
                .map(itemType -> new CollectItemGoal(Ingredient.of(itemType), itemCounts.getInt(itemType)))
                .collect(Collectors.toList());
    }
}
