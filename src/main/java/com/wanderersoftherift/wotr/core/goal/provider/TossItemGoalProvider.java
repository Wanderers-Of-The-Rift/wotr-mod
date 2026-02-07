package com.wanderersoftherift.wotr.core.goal.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalProvider;
import com.wanderersoftherift.wotr.core.goal.type.TossItemGoal;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TossItemGoalProvider(FastWeightedList<Ingredient> item, NumberProvider count) implements GoalProvider {
    public static final MapCodec<TossItemGoalProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FastWeightedList.codecWithSingleAlternative(Ingredient.CODEC)
                    .fieldOf("item")
                    .forGetter(TossItemGoalProvider::item),
            NumberProviders.CODEC.fieldOf("count").forGetter(TossItemGoalProvider::count)
    ).apply(instance, TossItemGoalProvider::new));

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return CODEC;
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootContext context) {
        return List.of(new TossItemGoal(item.random(context.getRandom()), count.getInt(context)));
    }
}
