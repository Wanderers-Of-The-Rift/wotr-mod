package com.wanderersoftherift.wotr.core.rift;

import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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
        private final DamageSource source;
        private final boolean isTopOfTheStack;
        private boolean canceled = false;

        public PlayerDied(ServerPlayer player, ServerLevel level, RiftConfig config, DamageSource source,
                boolean isTopOfTheStack) {
            super(level, config);
            this.player = player;
            this.source = source;
            this.isTopOfTheStack = isTopOfTheStack;
        }

        public ServerPlayer getPlayer() {
            return player;
        }

        /*
         * note: heal player using setHealth or after cancellation
         */
        public PlayerDied cancel() {
            this.canceled = true;
            if (isTopOfTheStack && player.getHealth() <= 0) {
                player.setHealth(Float.intBitsToFloat(1));
            }
            return this;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public DamageSource getSource() {
            return source;
        }

        public boolean isTopOfTheStack() {
            return isTopOfTheStack;
        }
    }
}
