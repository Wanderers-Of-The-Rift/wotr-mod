package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizer;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public class RingLayer implements LayeredRiftLayout.LayoutLayer {
    private final RoomRandomizer.Factory roomRandomizerFactory;
    private final int radius;
    private volatile RoomRandomizer roomRandomizer;

    public RingLayer(RoomRandomizer.Factory roomRandomizer, int radius) {
        this.roomRandomizerFactory = roomRandomizer;
        this.radius = radius;
    }

    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces,
            MinecraftServer server) {
        if (roomRandomizer == null) {
            synchronized (this) {
                if (roomRandomizer == null) {
                    roomRandomizer = roomRandomizerFactory.createRandomizer(server);
                }
            }
        }
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
}
