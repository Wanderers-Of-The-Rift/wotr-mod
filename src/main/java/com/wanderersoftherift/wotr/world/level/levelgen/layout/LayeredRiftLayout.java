package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.layout.shape.FiniteRiftShape;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

public interface LayeredRiftLayout extends RiftLayout {

    interface LayoutLayer {
        void generateSection(
                LayoutSection section,
                RandomSource source,
                ArrayList<RiftSpace> allSpaces,
                MinecraftServer server);
    }

    interface LayoutSection {
        FiniteRiftShape sectionShape();

        boolean tryPlaceSpace(RiftSpace space);

        long[] getEmptySpaces();
    }

}
