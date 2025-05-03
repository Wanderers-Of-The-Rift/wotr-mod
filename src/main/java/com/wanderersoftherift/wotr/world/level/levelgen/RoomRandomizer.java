package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

public interface RoomRandomizer {

    RoomRiftSpace randomSpace(RoomRiftSpace.RoomType roomType, RandomSource source, Vec3i maximumSize);
}
