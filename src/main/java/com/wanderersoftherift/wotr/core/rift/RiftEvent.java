package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

public abstract class RiftEvent extends Event {

    private ServerLevel level;
    private RiftConfig config;

    public RiftEvent(ServerLevel level, RiftConfig config) {
        this.level = level;
        this.config = config;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public RiftConfig getConfig() {
        return config;
    }

    /**
     * Event when a new rift has been created
     */
    public static class Created extends RiftEvent {
        public Created(ServerLevel level, RiftConfig config) {
            super(level, config);
        }
    }

    /**
     * Event when a rift is being destroyed
     */
    public static class Closing extends RiftEvent {
        public Closing(ServerLevel level, RiftConfig config) {
            super(level, config);
        }
    }

    /**
     * Event when a player dies in a rift
     */
    public static class PlayerDied extends RiftEvent {

        private ServerPlayer player;

        public PlayerDied(ServerPlayer player, ServerLevel level, RiftConfig config) {
            super(level, config);
            this.player = player;
        }

        public ServerPlayer getPlayer() {
            return player;
        }
    }
}
