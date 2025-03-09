package com.wanderersoftherift.wotr.item.socket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

// Vanilla Equivalent ItemEnchantments
public record GearSockets(List<GearSocket> sockets) {
    public static Codec<GearSockets> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets)
    ).apply(inst, GearSockets::new));

    /*public static StreamCodec<RegistryFriendlyByteBuf, GearSockets> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.
            GearSockets::new
    );*/
}
