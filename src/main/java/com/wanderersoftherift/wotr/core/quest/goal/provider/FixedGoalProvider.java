package com.wanderersoftherift.wotr.core.quest.goal.provider;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.core.quest.GoalProvider;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A provider that wraps a single goal
 * 
 * @param goal
 * @param <T>
 */
public record FixedGoalProvider<T extends Goal>(T goal) implements GoalProvider {

    private static final Map<MapCodec<? extends Goal>, MapCodec<FixedGoalProvider<?>>> CODEC_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Goal> MapCodec<FixedGoalProvider<T>> codec(MapCodec<T> goalCodec) {
        return (MapCodec) CODEC_CACHE.computeIfAbsent(goalCodec,
                (gc) -> (MapCodec<FixedGoalProvider<?>>) (MapCodec) generateCodec(gc));
    }

    @Override
    public MapCodec<? extends GoalProvider> getCodec() {
        return codec(goal.getType().codec());
    }

    @Override
    public @NotNull List<Goal> generateGoal(LootParams params) {
        return List.of(goal);
    }

    private static <T extends Goal> MapCodec<FixedGoalProvider<T>> generateCodec(MapCodec<T> goalCodec) {
        return goalCodec.xmap(FixedGoalProvider::new, FixedGoalProvider::goal);
    }
}
