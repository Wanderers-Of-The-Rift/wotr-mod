package com.wanderersoftherift.wotr.core.guild;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.network.guild.GuildStatusReplicationPayload;
import com.wanderersoftherift.wotr.network.guild.GuildStatusUpdatePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Attachment for tracking guild status of an entity (generally player)
 */
public class GuildStatus {
    private final IAttachmentHolder holder;

    private final Object2IntMap<Holder<Guild>> reputation = new Object2IntArrayMap<>();
    private final Object2IntMap<Holder<Guild>> ranks = new Object2IntArrayMap<>();

    public GuildStatus(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private GuildStatus(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            reputation.putAll(data.reputation);
            ranks.putAll(data.rank);
        }
    }

    /**
     * @param guild
     * @return The current reputation with the guild
     */
    public int getReputation(Holder<Guild> guild) {
        return reputation.getOrDefault(guild, 0);
    }

    /**
     * @param guild  The guild to add reputation for
     * @param amount Amount of reputation to add
     */
    public void addReputation(Holder<Guild> guild, int amount) {
        setReputation(guild, reputation.getOrDefault(guild, 0) + amount);
    }

    /**
     * Sets the reputation the holder has with a guild. This may trigger rank up.
     * 
     * @param guild The guild to set reputation for
     * @param value
     */
    public void setReputation(Holder<Guild> guild, int value) {
        if (value == reputation.put(guild, value)) {
            return;
        }
        int currentRank = ranks.getOrDefault(guild, 0);
        List<GuildRank> guildRanks = guild.value().ranks();
        int newRank = currentRank;
        while (newRank < guildRanks.size() && guildRanks.get(newRank).reputationRequirement() <= value) {
            newRank++;
        }
        if (newRank != currentRank) {
            setRank(guild, newRank);
        } else if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new GuildStatusUpdatePayload(guild, value, currentRank));
        }
    }

    /**
     * @param guild
     * @return The holder's current rank in the given guild
     */
    public int getRank(Holder<Guild> guild) {
        return ranks.getOrDefault(guild, 0);
    }

    /**
     * Sets the holder's rank in the given guild
     * 
     * @param guild
     * @param rank
     */
    public void setRank(Holder<Guild> guild, int rank) {
        rank = Math.min(rank, guild.value().ranks().size());
        int oldRank = ranks.put(guild, rank);
        if (oldRank != rank) {
            if (holder instanceof Player player) {
                NeoForge.EVENT_BUS.post(new GuildEvent.RankChange(player, guild, oldRank, rank));
            }
            if (holder instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new GuildStatusUpdatePayload(guild, getReputation(guild), rank));
            }
        }
    }

    /**
     * Sets all the reputation and ranks (for use by server->client replication)
     * 
     * @param reputation
     * @param ranks
     */
    public void setAll(Map<Holder<Guild>, Integer> reputation, Map<Holder<Guild>, Integer> ranks) {
        this.reputation.clear();
        this.reputation.putAll(reputation);
        this.ranks.clear();
        this.ranks.putAll(ranks);
    }

    /**
     * Replicates the guild status to the client
     */
    public void replicate() {
        if (holder instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new GuildStatusReplicationPayload(reputation, ranks));
        }
    }

    /**
     * @return A list of all guilds with any non-zero reputation and/or rank
     */
    public List<Holder<Guild>> getGuildsWithStanding() {
        return reputation.keySet()
                .stream()
                .sorted(Comparator.comparing(guild -> ranks.getOrDefault(guild, 0))
                        .thenComparing(guild -> reputation.getOrDefault(guild, 0)))
                .toList();
    }

    public static IAttachmentSerializer<Tag, GuildStatus> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, GuildStatus::new, GuildStatus::getData);
    }

    private Data getData() {
        return new Data(reputation, ranks);
    }

    private record Data(Map<Holder<Guild>, Integer> reputation, Map<Holder<Guild>, Integer> rank) {
        private static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Guild.CODEC, Codec.INT).fieldOf("reputation").forGetter(Data::reputation),
                Codec.unboundedMap(Guild.CODEC, Codec.INT).fieldOf("ranks").forGetter(Data::rank)
        ).apply(instance, Data::new));
    }
}
