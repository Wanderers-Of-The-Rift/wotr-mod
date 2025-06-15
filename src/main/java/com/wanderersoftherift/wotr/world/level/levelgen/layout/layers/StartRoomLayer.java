package com.wanderersoftherift.wotr.world.level.levelgen.layout.layers;

import com.wanderersoftherift.wotr.world.level.levelgen.RoomRandomizer;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.LayeredRiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public class StartRoomLayer implements LayeredRiftLayout.LayoutLayer {
    private final RoomRandomizer portalRandomizer;

    public StartRoomLayer(RoomRandomizer portalRandomizer) {
        this.portalRandomizer = portalRandomizer;
    }

    @Override
    public void generateSection(
            LayeredRiftLayout.LayoutSection section,
            RandomSource source,
            ArrayList<RiftSpace> allSpaces) {
        var room = portalRandomizer.randomSpace(RoomRiftSpace.RoomType.PORTAL, source, new Vec3i(3, 3, 3))
                .offset(-1, -1, -1);
        if (section.tryPlaceSpace(room)) {
            allSpaces.add(room);
        }
    }
}
