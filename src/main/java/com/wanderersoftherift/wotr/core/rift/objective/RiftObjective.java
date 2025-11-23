package com.wanderersoftherift.wotr.core.rift.objective;

import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalTracking;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Function;

public class RiftObjective implements GoalTracking {
    @Override
    public <T extends Goal> void progressGoals(Class<T> type, Function<T, Integer> amount, ServerLevel level) {

    }
}
