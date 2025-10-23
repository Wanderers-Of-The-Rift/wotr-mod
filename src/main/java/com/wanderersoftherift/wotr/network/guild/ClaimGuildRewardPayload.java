package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClaimGuildRewardPayload(Holder<Guild> guild) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClaimGuildRewardPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "claim_guild_reward"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClaimGuildRewardPayload> STREAM_CODEC = StreamCodec
            .composite(Guild.STREAM_CODEC, ClaimGuildRewardPayload::guild, ClaimGuildRewardPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnServer(final IPayloadContext context) {
        context.player().getExistingData(WotrAttachments.UNCLAIMED_GUILD_REWARDS).ifPresent(unclaimedGuildRewards -> {
            unclaimedGuildRewards.claimRewards(guild);
        });
    }
}
