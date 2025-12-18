package com.wanderersoftherift.wotr.core.goal;

/**
 * Provides information relating to the state of a goal
 */
public interface GoalState<T extends Goal> {

    /**
     * @return The goal
     */
    T getGoal();

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

    /**
     * Increments progress by 1
     */
    default void incrementProgress() {
        setProgress(getProgress() + 1);
    }

    /**
     * Sets progress to the specified amount, clamped between 0 and the goal's count
     * 
     * @param amount
     */
    void setProgress(int amount);
}
