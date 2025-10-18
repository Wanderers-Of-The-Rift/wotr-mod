package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.GuildInfo;
import com.wanderersoftherift.wotr.core.guild.GuildStatus;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record GuildStatusUpdatePayload(Holder<GuildInfo> guild, int reputation, int rank)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GuildStatusUpdatePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "guild_status_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GuildStatusUpdatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    GuildInfo.STREAM_CODEC, GuildStatusUpdatePayload::guild, ByteBufCodecs.INT,
                    GuildStatusUpdatePayload::reputation, ByteBufCodecs.INT, GuildStatusUpdatePayload::rank,
                    GuildStatusUpdatePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        GuildStatus guildStatus = context.player().getData(WotrAttachments.GUILD_STATUS.get());
        guildStatus.setReputation(guild, reputation);
        guildStatus.setRank(guild, rank);
    }

}
