package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

@Deprecated
public interface LegacyRoomRandomizer {

    RoomRiftSpace randomSpace(
            @Deprecated RoomRiftSpace.RoomType roomType /* use randomizer per room type instead */,
            RandomSource source,
            Vec3i maximumSize);
}
