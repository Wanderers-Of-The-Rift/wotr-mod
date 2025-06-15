package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

public interface RoomRandomizer {

    RoomRiftSpace randomSpace(
            @Deprecated RoomRiftSpace.RoomType roomType /* use randomizer per room type instead */,
            RandomSource source,
            Vec3i maximumSize);
}
