package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A manager for goals and goal trackers
 */
public class GoalManager {
    private static final List<Function<Player, Stream<? extends GoalTracker>>> goalTrackerLookup = new ArrayList<>();

    static void registerGoalTrackerLookup(Function<Player, Stream<? extends GoalTracker>> lookup) {
        goalTrackerLookup.add(lookup);
    }

    public static Stream<GoalTracker> getGoalTrackers(Player player) {
        return goalTrackerLookup.stream().flatMap(func -> func.apply(player));
    }

    public static <T extends Goal> Stream<GoalState<T>> getGoalStates(Player player, Class<T> goalType) {
        return getGoalTrackers(player).flatMap(tracker -> tracker.streamGoals(goalType));
    }
}
