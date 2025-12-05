package com.wanderersoftherift.wotr.core.rift.map;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

public abstract class RiftMapEvent extends Event {
    private final Level level;
    private final RoomRiftSpace room;

    public RiftMapEvent(Level level, RoomRiftSpace room) {
        this.level = level;
        this.room = room;
    }

    /**
     * @return The level the event occurred within
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return The room the event concerns
     */
    public RoomRiftSpace getRoom() {
        return room;
    }

    /**
     * Event when a room is discovered (but not necessarily visited)
     */
    public static class RoomDiscovered extends RiftMapEvent {
        private final Player player;

        public RoomDiscovered(Level level, RoomRiftSpace room, Player player) {
            super(level, room);
            this.player = player;
        }

        /**
         * @return The player that discovered the room
         */
        public Player getPlayer() {
            return player;
        }
    }

    /**
     * Event when a room is visited for the first time
     */
    public static class RoomFirstVisited extends RiftMapEvent {

        private final Player player;

        public RoomFirstVisited(Level level, RoomRiftSpace room, Player player) {
            super(level, room);
            this.player = player;
        }

        /**
         * @return The player that visited the room
         */
        public Player getPlayer() {
            return player;
        }
    }

}
