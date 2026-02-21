package com.wanderersoftherift.wotr.core.goal;

import java.util.stream.Stream;

/**
 * Interface for objects (primarily attachments) that track goal progress
 */
public interface GoalTracker {

    /**
     * @param <T>
     * @param goalType
     * @return a stream of goals of the provided type that are being tracked by this GoalTracker
     */
    <T extends Goal> Stream<? extends GoalState<T>> streamGoals(Class<T> goalType);
}
