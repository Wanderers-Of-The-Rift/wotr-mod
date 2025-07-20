package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

import javax.annotation.Nonnull;

public abstract class RiftEvent extends Event {

    protected RiftConfig config;
    private ServerLevel level;

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
    public abstract static class Created extends RiftEvent {
        private final ServerPlayer firstPlayer;

        public Created(ServerLevel level, RiftConfig config, ServerPlayer firstPlayer) {
            super(level, config);
            this.firstPlayer = firstPlayer;
        }

        public ServerPlayer getFirstPlayer() {
            return firstPlayer;
        }

        public static class Pre extends Created {

            public Pre(RiftConfig config, ServerPlayer firstPlayer) {
                super(null, config, firstPlayer);
            }

            public void setConfig(RiftConfig config) {
                this.config = config;
            }
        }

        public static class Post extends Created {

            public Post(@Nonnull ServerLevel level, RiftConfig config, ServerPlayer firstPlayer) {
                super(level, config, firstPlayer);
            }
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
