package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizer;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizerImpl;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public class PredefinedRoomLayer implements LayeredRiftLayout.LayoutLayer {
    private final RoomRandomizer portalRandomizer;
    private final Vec3i position;

    public PredefinedRoomLayer(RoomRandomizer portalRandomizer, Vec3i position) {
        this.portalRandomizer = portalRandomizer;
        this.position = position;
    }

    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces) {
        var room = portalRandomizer.randomSpace(source, new Vec3i(3, 3, 3))
                .offset(position.getX(), position.getY(), position.getZ());
        if (section.tryPlaceSpace(room)) {
            allSpaces.add(room);
        }
    }

    public static record Factory(RoomRandomizerImpl.Factory roomRandomizerFactory, Vec3i position)
            implements LayeredRiftLayout.LayoutLayer.Factory {

        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(it -> it.group(
                RoomRandomizerImpl.Factory.CODEC.fieldOf("room_randomizer").forGetter(Factory::roomRandomizerFactory),
                Codec.INT.fieldOf("room_x").forGetter(it2 -> it2.position.getX()),
                Codec.INT.fieldOf("room_y").forGetter(it2 -> it2.position.getY()),
                Codec.INT.fieldOf("room_z").forGetter(it2 -> it2.position.getZ())
        ).apply(it, (room, x, y, z) -> new Factory(room, new Vec3i(x, y, z))));

        @Override
        public LayeredRiftLayout.LayoutLayer createLayer(MinecraftServer server, RiftConfig riftConfig) {
            return new PredefinedRoomLayer(roomRandomizerFactory.createRandomizer(server), position);
        }

        @Override
        public MapCodec<? extends LayeredRiftLayout.LayoutLayer.Factory> codec() {
            return CODEC;
        }
    }
}
