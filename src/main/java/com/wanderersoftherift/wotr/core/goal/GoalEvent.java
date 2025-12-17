package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import java.util.function.ToIntFunction;

/**
 * Events relating to goals
 */
public abstract class GoalEvent extends Event {

    /**
     * Event when an update occurs relating to a goal type
     * 
     * @param <T>
     */
    public static class Update<T extends Goal> extends GoalEvent implements ICancellableEvent {
        private final Player player;
        private final Class<T> goalType;
        private final ToIntFunction<T> progressFunction;

        public Update(Player player, Class<T> goalType, ToIntFunction<T> progressFunction) {
            this.player = player;
            this.goalType = goalType;
            this.progressFunction = progressFunction;
        }

        public Player getPlayer() {
            return player;
        }

        public Class<T> getGoalType() {
            return goalType;
        }

        public ToIntFunction<T> getProgressFunction() {
            return progressFunction;
        }

        public void progressGoals(GoalTracker tracker) {
            if (player.level() instanceof ServerLevel level) {
                tracker.progressGoals(goalType, progressFunction, level);
            }
        }
    }
}
