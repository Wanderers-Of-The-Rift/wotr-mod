package com.wanderersoftherift.wotr.core.guild;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

/**
 * Events relating to guilds
 */
public abstract class GuildEvent extends Event {

    private Player player;
    private Holder<Guild> guild;

    public GuildEvent(Player player, Holder<Guild> guild) {
        this.player = player;
        this.guild = guild;
    }

    public Player player() {
        return player;
    }

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

        public int oldRank() {
            return oldRank;
        }

        public int newRank() {
            return newRank;
        }
    }
}
