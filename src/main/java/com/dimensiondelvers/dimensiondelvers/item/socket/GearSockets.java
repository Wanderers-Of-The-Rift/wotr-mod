package com.dimensiondelvers.dimensiondelvers.item.socket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

// Vanilla Equivalent ItemEnchantments
public record GearSockets(List<GearSocket> sockets) {
    public static Codec<GearSockets> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets)
    ).apply(inst, GearSockets::new));

    public GearSocket socket(int index) {
        if (index < 0 || index >= sockets.size()) {
            return null;
        }
        return sockets.get(index);
    }
}
