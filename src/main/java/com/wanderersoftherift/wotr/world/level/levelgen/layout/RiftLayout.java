package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.CorridorValidator;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;

public interface RiftLayout extends CorridorValidator {

    RiftSpace getChunkSpace(Vec3i pos);

}
