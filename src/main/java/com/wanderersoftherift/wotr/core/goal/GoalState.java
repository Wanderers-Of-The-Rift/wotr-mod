package com.wanderersoftherift.wotr.core.goal;

/**
 * Provides information relating to the state of a goal
 */
public interface GoalState {

    /**
     * @return The goal
     */
    Goal getGoal();

    /**
     * @return Whether the goal is complete
     */
    default boolean isComplete() {
        return getProgress() >= getGoal().count();
    }

    /**
     * @return The numeric progress of the goal
     */
    int getProgress();
}
