package com.wanderersoftherift.wotr.core.quest.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.entity.player.progression.ProgressionTrack;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

/**
 * This reward provides progression track points
 * 
 * @param track
 * @param amount
 */
public record ProgressionTrackPointReward(Holder<ProgressionTrack> track, int amount) implements Reward {
    public static final MapCodec<ProgressionTrackPointReward> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ProgressionTrack.CODEC.fieldOf("track").forGetter(ProgressionTrackPointReward::track),
                    Codec.intRange(1, Integer.MAX_VALUE)
                            .fieldOf("amount")
                            .forGetter(ProgressionTrackPointReward::amount)
            ).apply(instance, ProgressionTrackPointReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionTrackPointReward> STREAM_CODEC = StreamCodec
            .composite(
                    ProgressionTrack.STREAM_CODEC, ProgressionTrackPointReward::track, ByteBufCodecs.INT,
                    ProgressionTrackPointReward::amount, ProgressionTrackPointReward::new
            );

    public static final DualCodec<ProgressionTrackPointReward> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<ProgressionTrackPointReward> getType() {
        return TYPE;
    }

    @Override
    public void apply(Player player) {
        player.getData(WotrAttachments.PROGRESSION_TRACKER).incrementPoints(track, amount);
    }
}
