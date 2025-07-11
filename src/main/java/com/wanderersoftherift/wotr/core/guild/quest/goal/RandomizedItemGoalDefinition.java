package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import com.wanderersoftherift.wotr.core.guild.quest.GoalDefinition;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record RandomizedItemGoalDefinition(Ingredient item, NumberProvider count) implements GoalDefinition {

    public static final MapCodec<RandomizedItemGoalDefinition> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(RandomizedItemGoalDefinition::item),
                    NumberProviders.CODEC.fieldOf("count").forGetter(RandomizedItemGoalDefinition::count)
            ).apply(instance, RandomizedItemGoalDefinition::new));

    @Override
    public MapCodec<? extends GoalDefinition> getCodec() {
        return CODEC;
    }

    @Override
    public Goal generateGoal(LootContext context) {
        return new GiveItemGoal(item, count.getInt(context));
    }
}
