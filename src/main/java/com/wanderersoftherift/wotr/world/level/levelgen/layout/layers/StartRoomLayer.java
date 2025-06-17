package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.randomizers.RoomRandomizer;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public class StartRoomLayer implements LayeredRiftLayout.LayoutLayer {
    private final RoomRandomizer.Factory portalRandomizerFactory;
    private volatile RoomRandomizer portalRandomizer;

    public StartRoomLayer(RoomRandomizer.Factory portalRandomizer) {
        this.portalRandomizerFactory = portalRandomizer;
    }

    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces,
            MinecraftServer server) {
        if (portalRandomizer == null) {
            synchronized (this) {
                if (portalRandomizer == null) {
                    portalRandomizer = portalRandomizerFactory.createRandomizer(server);
                }
            }
        }
        var room = portalRandomizer.randomSpace(source, new Vec3i(3, 3, 3)).offset(-1, -1, -1);
        if (section.tryPlaceSpace(room)) {
            allSpaces.add(room);
        }
    }
}
