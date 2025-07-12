package com.wanderersoftherift.wotr.core.guild.quest.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalProvider;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.Optional;

public record RandomizedItemGoalProvider(Ingredient item, NumberProvider count) implements GoalProvider {

    public static final MapCodec<RandomizedItemGoalProvider> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(RandomizedItemGoalProvider::item),
                    NumberProviders.CODEC.fieldOf("count").forGetter(RandomizedItemGoalProvider::count)
            ).apply(instance, RandomizedItemGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public List<Goal> generateGoal(LootParams params) {
        return List.of(new GiveItemGoal(item, count.getInt(new LootContext.Builder(params).create(Optional.empty()))));
    }
}
