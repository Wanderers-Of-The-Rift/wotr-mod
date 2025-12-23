package com.wanderersoftherift.wotr.entity.player.progression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.gui.menu.reward.RewardMenu;
import com.wanderersoftherift.wotr.network.guild.ProgressionTrackerReplicationPayload;
import com.wanderersoftherift.wotr.network.guild.ProgressionTrackerUpdatePayload;
import com.wanderersoftherift.wotr.serialization.AttachmentSerializerFromDataCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class ProgressionTracker {

    private final Map<Holder<ProgressionTrack>, ProgressionData> progressionLookup = new LinkedHashMap<>();
    private final IAttachmentHolder holder;

    public ProgressionTracker(@NotNull IAttachmentHolder holder) {
        this(holder, null);
    }

    private ProgressionTracker(@NotNull IAttachmentHolder holder, @Nullable Data data) {
        this.holder = holder;
        if (data != null) {
            progressionLookup.putAll(data.status);
        }
    }

    private ProgressionData getTrackData(Holder<ProgressionTrack> track) {
        return progressionLookup.computeIfAbsent(track, t -> new ProgressionData());
    }

    public int getPoints(Holder<ProgressionTrack> track) {
        return getTrackData(track).getPoints();
    }

    public void incrementPoints(Holder<ProgressionTrack> track, int amount) {
        setPoints(track, getTrackData(track).getPoints() + amount);
    }

    public void setPoints(Holder<ProgressionTrack> track, int value) {
        ProgressionData data = getTrackData(track);
        if (value == data.getPoints()) {
            return;
        }
        data.setPoints(value);
        int newRank = data.getRank();
        int nextRank = newRank + 1;
        while (nextRank < track.value().ranks().size() && value >= track.value().ranks().get(nextRank).requirement()) {
            newRank = nextRank++;
        }

        if (newRank != data.getRank()) {
            setRank(track, newRank);
        } else if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ProgressionTrackerUpdatePayload(track, data));
        }
    }

    /**
     * @param track
     * @return The holder's current rank in the given track
     */
    public int getRank(Holder<ProgressionTrack> track) {
        return getTrackData(track).getRank();
    }

    /**
     * Sets the holder's rank for the given track
     *
     * @param track
     * @param rank
     */
    public void setRank(Holder<ProgressionTrack> track, int rank) {
        ProgressionData data = getTrackData(track);
        rank = Math.min(rank, track.value().ranks().size() - 1);
        int oldRank = data.getRank();
        if (oldRank != rank) {
            data.setRank(rank);
            data.setClaimedRank(Math.min(rank, data.getClaimedRank()));
            if (holder instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new ProgressionTrackerUpdatePayload(track, data));
            }
        }
    }

    public boolean hasUnclaimedRewards(Holder<ProgressionTrack> track) {
        ProgressionData trackData = getTrackData(track);
        for (int rank = trackData.claimedRank + 1; rank <= trackData.rank; rank++) {
            if (!track.value().ranks().get(rank).rewards().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void claimRewards(Holder<ProgressionTrack> track) {
        if (!(holder instanceof Player player) || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        ProgressionData trackData = getTrackData(track);
        if (trackData.claimedRank == trackData.rank) {
            return;
        }
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        LootContext context = new LootContext.Builder(params).create(Optional.empty());
        List<Reward> rewards = IntStream.rangeClosed(trackData.claimedRank + 1, trackData.rank)
                .mapToObj(i -> track.value().ranks().get(i))
                .flatMap(rank -> rank.rewards().stream())
                .flatMap(rewardProvider -> rewardProvider.generateReward(context).stream())
                .toList();
        trackData.claimedRank = trackData.rank;
        replicate(track);
        if (!rewards.isEmpty()) {
            RewardMenu.openRewardMenu(player, rewards,
                    Component.translatable(WanderersOfTheRift.translationId("container", "rank_up")));
        }
    }

    public void replicate() {
        if (holder instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ProgressionTrackerReplicationPayload(progressionLookup));
        }
    }

    private void replicate(Holder<ProgressionTrack> track) {
        if (holder instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new ProgressionTrackerUpdatePayload(track, getTrackData(track)));
        }
    }

    public void setAll(Map<Holder<ProgressionTrack>, ProgressionData> replicatedStatus) {
        progressionLookup.clear();
        progressionLookup.putAll(replicatedStatus);
    }

    public void setData(Holder<ProgressionTrack> track, ProgressionData data) {
        progressionLookup.put(track, data);
    }

    public static class ProgressionData {
        public static final Codec<ProgressionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("points").forGetter(ProgressionData::getPoints),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("rank").forGetter(ProgressionData::getRank),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("claimed_rank").forGetter(ProgressionData::getClaimedRank)
        ).apply(instance, ProgressionData::new));

        public static final StreamCodec<ByteBuf, ProgressionData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ProgressionData::getPoints, ByteBufCodecs.INT, ProgressionData::getRank,
                ByteBufCodecs.INT, ProgressionData::getClaimedRank, ProgressionData::new
        );

        private int points;
        private int rank;
        private int claimedRank;

        public ProgressionData() {

        }

        public ProgressionData(int points, int rank, int claimedRank) {
            this.points = points;
            this.rank = rank;
            this.claimedRank = claimedRank;
        }

        public int getPoints() {
            return points;
        }

        public int getRank() {
            return rank;
        }

        public int getClaimedRank() {
            return claimedRank;
        }

        private void setPoints(int points) {
            this.points = points;
        }

        private void setRank(int rank) {
            this.rank = rank;
        }

        private void setClaimedRank(int claimedRank) {
            this.claimedRank = claimedRank;
        }
    }

    public static IAttachmentSerializer<Tag, ProgressionTracker> getSerializer() {
        return new AttachmentSerializerFromDataCodec<>(Data.CODEC, ProgressionTracker::new,
                ProgressionTracker::getData);
    }

    private Data getData() {
        return new Data(progressionLookup);
    }

    private record Data(Map<Holder<ProgressionTrack>, ProgressionData> status) {
        private static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(ProgressionTrack.CODEC, ProgressionData.CODEC)
                        .fieldOf("status")
                        .forGetter(Data::status)
        ).apply(instance, Data::new));
    }

}
