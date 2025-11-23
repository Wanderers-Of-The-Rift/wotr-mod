package com.wanderersoftherift.wotr.core.goal;

public interface GoalState {

    Goal getGoal();

    default boolean isComplete() {
        return getProgress() >= getGoal().count();
    }

    int getProgress();
}
