package com.wanderersoftherift.wotr.core.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

import java.util.function.Function;

public abstract class GoalEvent extends Event {
    public static class Update<T extends Goal> extends GoalEvent {
        private final Player player;
        private final Class<T> goalType;
        private final Function<T, Integer> progressFunction;
        private boolean cancelled;

        public Update(Player player, Class<T> goalType, Function<T, Integer> progressFunction) {
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

        public Function<T, Integer> getProgressFunction() {
            return progressFunction;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public void progressGoals(GoalTracker tracker) {
            if (player.level() instanceof ServerLevel level) {
                tracker.progressGoals(goalType, progressFunction, level);
            }
        }
    }
}
