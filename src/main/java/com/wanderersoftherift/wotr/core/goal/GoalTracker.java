package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.server.level.ServerLevel;

import java.util.function.ToIntFunction;

/**
 * Interface for objects (primarily attachments) that track goal progress
 */
public interface GoalTracker {
    <T extends Goal> void progressGoals(Class<T> type, ToIntFunction<T> amount, ServerLevel level);
}
