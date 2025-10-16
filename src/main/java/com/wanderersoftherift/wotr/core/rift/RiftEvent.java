package com.wanderersoftherift.wotr.core.rift;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
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
        private final Player firstPlayer;

        public Created(ServerLevel level, RiftConfig config, Player firstPlayer) {
            super(level, config);
            this.firstPlayer = firstPlayer;
        }

        public Player getFirstPlayer() {
            return firstPlayer;
        }

        /**
         * Invoked before the {@link ServerLevel} and {@link com.wanderersoftherift.wotr.world.level.FastRiftGenerator}
         * of the rift are created. Could be used for modifying {@link RiftConfig} of the rift (some examples are in
         * {@link RiftInitializationEvents}). {@link #getLevel()} will return null.
         */
        public static class Pre extends Created {

            public Pre(RiftConfig config, Player firstPlayer) {
                super(null, config, firstPlayer);
            }

            public void setConfig(RiftConfig config) {
                this.config = config;
            }
        }

        /**
         * Invoked after the {@link ServerLevel} of the rift is created. Could be used for adding custom
         * {@link net.minecraft.world.level.saveddata.SavedData} or attachments to the newly-created rift or modifying
         * built-in {@link RiftData}.
         */
        public static class Post extends Created {

            public Post(@Nonnull ServerLevel level, RiftConfig config, Player firstPlayer) {
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
     * Event when a player completes (with or without objective) a rift
     */
    public static class PlayerCompletedRift extends RiftEvent {
        private final ServerPlayer player;
        private final boolean objectiveComplete;

        public PlayerCompletedRift(ServerPlayer player, boolean objectiveComplete, ServerLevel level,
                RiftConfig config) {
            super(level, config);
            this.player = player;
            this.objectiveComplete = objectiveComplete;
        }

        public ServerPlayer getPlayer() {
            return player;
        }

        public boolean isObjectiveComplete() {
            return objectiveComplete;
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
