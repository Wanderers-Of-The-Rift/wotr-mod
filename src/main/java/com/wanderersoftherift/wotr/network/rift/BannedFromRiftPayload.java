package com.wanderersoftherift.wotr.network.rift;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.rift.BannedRiftList;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.core.rift.RiftLevelManager;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record BannedFromRiftPayload(List<ResourceLocation> ids) implements CustomPacketPayload {

    public static final Type<BannedFromRiftPayload> TYPE = new Type<>(WanderersOfTheRift.id("banned_from_rift"));
    public static final StreamCodec<ByteBuf, BannedFromRiftPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), BannedFromRiftPayload::ids,
            BannedFromRiftPayload::new);

    public static void sendTo(ServerPlayer player) {
        BannedFromRiftPayload payload = new BannedFromRiftPayload(player.serverLevel()
                .getServer()
                .levelKeys()
                .stream()
                .map(x -> RiftLevelManager.getRiftLevel(x.location()))
                .filter(Objects::nonNull)
                .filter(x -> RiftData.get(x).isBannedFromRift(player))
                .map(x -> x.dimension().location())
                .toList());
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        context.player().setData(WotrAttachments.BANNED_RIFTS, new BannedRiftList(ids));
    }
}
