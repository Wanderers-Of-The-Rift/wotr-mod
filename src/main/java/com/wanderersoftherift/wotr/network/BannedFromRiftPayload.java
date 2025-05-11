package com.wanderersoftherift.wotr.network;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.rift.BannedRiftList;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BannedFromRiftPayload(List<ResourceLocation> ids) implements CustomPacketPayload {

    public static final Type<BannedFromRiftPayload> TYPE = new Type<>(WanderersOfTheRift.id("banned_from_rift"));
    public static final StreamCodec<ByteBuf, BannedFromRiftPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), BannedFromRiftPayload::ids,
            BannedFromRiftPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleOnClient(IPayloadContext context) {
        BannedRiftList.addBannedRifts(ids);
    }
}
