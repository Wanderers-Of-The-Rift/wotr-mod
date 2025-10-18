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
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GuildStatus {
    private final IAttachmentHolder holder;

    private final Object2IntMap<Holder<GuildInfo>> reputation = new Object2IntArrayMap<>();
    private final Object2IntMap<Holder<GuildInfo>> ranks = new Object2IntArrayMap<>();

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

    public int getReputation(Holder<GuildInfo> guild) {
        return reputation.getOrDefault(guild, 0);
    }

    public void addReputation(Holder<GuildInfo> guild, int amount) {
        setReputation(guild, reputation.getOrDefault(guild, 0) + amount);
    }

    public void setReputation(Holder<GuildInfo> guild, int value) {
        int previous = reputation.put(guild, value);
        if (previous == value) {
            return;
        }
        int currentRank = ranks.getOrDefault(guild, 0);
        List<GuildRank> guildRanks = guild.value().ranks();
        int newRank = currentRank;
        while (newRank < guildRanks.size() && guildRanks.get(newRank).reputationRequirement() < value) {
            newRank++;
        }
        if (newRank != currentRank) {
            setRank(guild, newRank);
        } else if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new GuildStatusUpdatePayload(guild, value, currentRank));
        }
    }

    public int getRank(Holder<GuildInfo> guild) {
        return ranks.getOrDefault(guild, 0);
    }

    public void setRank(Holder<GuildInfo> guild, int rank) {
        rank = Math.min(rank, guild.value().ranks().size());
        int oldRank = ranks.put(guild, rank);
        if (oldRank != rank) {
            // TODO: provide rewards
            if (holder instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new GuildStatusUpdatePayload(guild, getReputation(guild), rank));
            }
        }
    }

    public static IAttachmentSerializer<Tag, GuildStatus> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, GuildStatus::new, GuildStatus::getData);
    }

    private Data getData() {
        return new Data(reputation, ranks);
    }

    public void setAll(Map<Holder<GuildInfo>, Integer> reputation, Map<Holder<GuildInfo>, Integer> ranks) {
        this.reputation.clear();
        this.reputation.putAll(reputation);
        this.ranks.clear();
        this.ranks.putAll(ranks);
    }

    public void replicate() {
        if (holder instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new GuildStatusReplicationPayload(reputation, ranks));
        }
    }

    public List<Holder<GuildInfo>> getGuilds() {
        return reputation.keySet()
                .stream()
                .sorted(Comparator.comparing(guild -> ranks.getOrDefault(guild, 0))
                        .thenComparing(guild -> reputation.getOrDefault(guild, 0)))
                .toList();
    }

    private record Data(Map<Holder<GuildInfo>, Integer> reputation, Map<Holder<GuildInfo>, Integer> rank) {
        private static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(GuildInfo.CODEC, Codec.INT).fieldOf("reputation").forGetter(Data::reputation),
                Codec.unboundedMap(GuildInfo.CODEC, Codec.INT).fieldOf("ranks").forGetter(Data::rank)
        ).apply(instance, Data::new));
    }
}
