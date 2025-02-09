package com.dimensiondelvers.dimensiondelvers.item.socket;

import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Vanilla Equivalent ItemEnchantments
public record GearSockets(List<GearSocket> sockets) {
    public static Codec<GearSockets> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GearSocket.CODEC.listOf().fieldOf("sockets").forGetter(GearSockets::sockets)
    ).apply(inst, GearSockets::new));

    public static GearSockets randomSockets() {
        Random random = new Random();
        int count = random.nextInt(6);
        ArrayList<GearSocket> sockets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RunegemShape[] shapes = RunegemShape.values();
            RunegemShape shape = shapes[random.nextInt(shapes.length)];
            sockets.add(new GearSocket(shape, null, null));
        }
        return new GearSockets(sockets);
    }

    public static GearSockets emptySockets() {
        return new GearSockets(new ArrayList<>());
    }
}
