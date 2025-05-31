package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.CorridorValidator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;

/**
 * decides and remembers room placement in a rift, each space should be provided by RoomRandomizer (or it should be
 * VoidSpace or null)
 */
public interface RiftLayout extends CorridorValidator {

    RiftSpace getChunkSpace(Vec3i pos);

}
