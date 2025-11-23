package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.server.level.ServerLevel;

import java.util.function.Function;

/**
 * Interface for objects (primarily attachments) that track goal progress
 */
public interface GoalTracking {
    <T extends Goal> void progressGoals(Class<T> type, Function<T, Integer> amount, ServerLevel level);
}
