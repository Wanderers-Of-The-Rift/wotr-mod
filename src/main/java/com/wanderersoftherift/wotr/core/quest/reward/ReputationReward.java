package com.wanderersoftherift.wotr.core.quest.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.quest.Reward;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

/**
 * This reward provides guild reputation
 * 
 * @param guild
 * @param amount
 */
public record ReputationReward(Holder<Guild> guild, int amount) implements Reward {
    public static final MapCodec<ReputationReward> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Guild.CODEC.fieldOf("guild").forGetter(ReputationReward::guild),
                    Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(ReputationReward::amount)
            ).apply(instance, ReputationReward::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReputationReward> STREAM_CODEC = StreamCodec.composite(
            Guild.STREAM_CODEC, ReputationReward::guild, ByteBufCodecs.INT, ReputationReward::amount,
            ReputationReward::new
    );

    public static final DualCodec<ReputationReward> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<ReputationReward> getType() {
        return TYPE;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public void apply(ServerPlayer player) {
        player.getData(WotrAttachments.GUILD_STATUS).addReputation(guild, amount);
    }
}
