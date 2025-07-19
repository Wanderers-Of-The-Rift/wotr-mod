package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public class RingLayer implements LayeredRiftLayout.LayoutLayer {
    private final int radius;
    private final RoomRandomizer roomRandomizer;

    public RingLayer(RoomRandomizer roomRandomizer, int radius) {
        this.roomRandomizer = roomRandomizer;
        this.radius = radius;
    }

    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces) {
        var origin = section.sectionShape().getBoxStart();
        var size = section.sectionShape().getBoxSize();

        var startX = origin.getX();
        var startZ = origin.getZ();
        while (Math.floorMod(startX, 3) != 2) {
            startX++;
        }
        while (Math.floorMod(startZ, 3) != 2) {
            startZ++;
        }

        for (int x = startX; x < size.getX(); x += 3) {
            for (int z = startZ; z < size.getZ(); z += 3) {
                RoomRiftSpace room = roomRandomizer.randomSpace(source, new Vec3i(3, 3, 3));
                room = room.offset(x, -room.corridors().getFirst().position().getY(), z);
                if ((x + 1) * (x + 1) + (z + 1) * (z + 1) < radius * radius && section.tryPlaceSpace(room)) {
                    allSpaces.add(room);
                }
            }
        }
    }

    public record Factory(RoomRandomizerImpl.Factory roomRandomizerFactory, int radius)
            implements LayeredRiftLayout.LayoutLayer.Factory {

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                RoomRandomizerImpl.Factory.CODEC.fieldOf("room_randomizer").forGetter(Factory::roomRandomizerFactory),
                Codec.INT.fieldOf("radius").forGetter(Factory::radius)
        ).apply(it, Factory::new));

        @Override
        public LayeredRiftLayout.LayoutLayer createLayer(MinecraftServer server) {
            return new RingLayer(roomRandomizerFactory.createRandomizer(server), radius);
        }

        @Override
        public MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory> codec() {
            return CODEC;
        }
    }
}
