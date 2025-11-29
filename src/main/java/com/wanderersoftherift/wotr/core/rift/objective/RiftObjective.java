package com.wanderersoftherift.wotr.core.rift.objective;

import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalTracker;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Function;

public class RiftObjective implements GoalTracker {
    @Override
    public <T extends Goal> void progressGoals(Class<T> type, Function<T, Integer> amount, ServerLevel level) {

    }
}
