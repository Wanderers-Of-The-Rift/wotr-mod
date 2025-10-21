package com.wanderersoftherift.wotr.network.guild;

import com.google.common.collect.ListMultimap;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.network.codec.GuavaStreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UnclaimedGuildRewardsReplicationPayload(ListMultimap<Holder<Guild>, Integer> unclaimedRankRewards)
        implements CustomPacketPayload {
    public static final Type<UnclaimedGuildRewardsReplicationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "unclaimed_guild_rewards_replication"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UnclaimedGuildRewardsReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(
                    GuavaStreamCodecs.listMultimap(Guild.STREAM_CODEC, ByteBufCodecs.INT.apply(ByteBufCodecs.list())),
                    UnclaimedGuildRewardsReplicationPayload::unclaimedRankRewards,
                    UnclaimedGuildRewardsReplicationPayload::new);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.UNCLAIMED_GUILD_REWARDS).setAll(unclaimedRankRewards);
    }
}
