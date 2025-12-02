package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

/**
 * Events relating to anomalies
 */
public abstract class AnomalyEvent extends Event {
    private final Level level;
    private final BlockPos pos;
    private final AnomalyTask.AnomalyTaskType<?> taskType;

    public AnomalyEvent(Level level, BlockPos pos, AnomalyTask.AnomalyTaskType<?> taskType) {
        this.level = level;
        this.pos = pos;
        this.taskType = taskType;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    // TODO: change this to AnomalyTask?
    public AnomalyTask.AnomalyTaskType<?> getTaskType() {
        return taskType;
    }

    /**
     * Event when an anomaly is closed (completed)
     */
    public static class Closed extends AnomalyEvent {
        private final Player closingPlayer;

        public Closed(Level level, BlockPos pos, AnomalyTask.AnomalyTaskType<?> task, Player closingPlayer) {
            super(level, pos, task);
            this.closingPlayer = closingPlayer;
        }

        public Player getClosingPlayer() {
            return closingPlayer;
        }
    }
}
