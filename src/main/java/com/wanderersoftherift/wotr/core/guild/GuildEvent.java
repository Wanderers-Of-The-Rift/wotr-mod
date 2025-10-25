package com.wanderersoftherift.wotr.core.guild;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

/**
 * Events relating to guilds
 */
public abstract class GuildEvent extends Event {

    private final Player player;
    private final Holder<Guild> guild;

    public GuildEvent(Player player, Holder<Guild> guild) {
        this.player = player;
        this.guild = guild;
    }

    /**
     * @return The player involved
     */
    public Player player() {
        return player;
    }

    /**
     * @return The guild involved
     */
    public Holder<Guild> guild() {
        return guild;
    }

    /**
     * Event when a player ranks up in a guild
     */
    public static class RankChange extends GuildEvent {
        private final int oldRank;
        private final int newRank;

        public RankChange(Player player, Holder<Guild> guild, int oldRank, int newRank) {
            super(player, guild);
            this.oldRank = oldRank;
            this.newRank = newRank;
        }

        /**
         * @return The player's previous rank
         */
        public int oldRank() {
            return oldRank;
        }

        /**
         * @return The player's new rank
         */
        public int newRank() {
            return newRank;
        }
    }
}
