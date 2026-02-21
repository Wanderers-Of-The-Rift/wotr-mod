package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.world.entity.player.Player;

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
     * 
     * @param player The player who is incrementing the progress
     */
    default void incrementProgress(Player player) {
        setProgress(player, getProgress() + 1);
    }

    /**
     * Sets progress to the specified amount, clamped between 0 and the goal's count
     *
     * @param player The player who is changing the progress (to support multi-player goals)
     * @param amount
     */
    void setProgress(Player player, int amount);
}
