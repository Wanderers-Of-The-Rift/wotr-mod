package com.wanderersoftherift.wotr.core.quest.goal.provider;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.core.quest.GoalProvider;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A provider that wraps a single goal
 * 
 * @param goal
 * @param <T>
 */
public record FixedGoalProvider<T extends Goal>(T goal) implements GoalProvider {

    public static <T extends Goal> MapCodec<FixedGoalProvider<T>> codec(MapCodec<T> goalCodec) {
        return goalCodec.xmap(FixedGoalProvider::new, FixedGoalProvider::goal);
    }

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return codec(goal.getType().codec());
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootParams params) {
        return List.of(goal);
    }
}
