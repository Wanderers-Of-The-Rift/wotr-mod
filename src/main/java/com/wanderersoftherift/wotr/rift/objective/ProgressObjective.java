package com.wanderersoftherift.wotr.rift.objective;

/**
 * Interface for objectives with progress that is tracked numerically against some goal
 */
public interface ProgressObjective extends OngoingObjective {

    /**
     * @return Current progress towards the target
     */
    int getCurrentProgress();

    /**
     * @return Target progress for completion
     */
    int getTargetProgress();

    /**
     * @return Whether the objective is complete
     */
    default boolean isComplete() {
        return getCurrentProgress() >= getTargetProgress();
    }
}
