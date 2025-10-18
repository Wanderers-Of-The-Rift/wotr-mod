package com.wanderersoftherift.wotr.network.guild;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.GuildInfo;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public record GuildStatusReplicationPayload(Map<Holder<GuildInfo>, Integer> reputation,
        Map<Holder<GuildInfo>, Integer> ranks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GuildStatusReplicationPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, "guild_status_replication"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GuildStatusReplicationPayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.map(LinkedHashMap::new, GuildInfo.STREAM_CODEC, ByteBufCodecs.INT),
                    GuildStatusReplicationPayload::reputation,
                    ByteBufCodecs.map(LinkedHashMap::new, GuildInfo.STREAM_CODEC, ByteBufCodecs.INT),
                    GuildStatusReplicationPayload::ranks, GuildStatusReplicationPayload::new);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(final IPayloadContext context) {
        context.player().getData(WotrAttachments.GUILD_STATUS.get()).setAll(reputation, ranks);
    }
}
