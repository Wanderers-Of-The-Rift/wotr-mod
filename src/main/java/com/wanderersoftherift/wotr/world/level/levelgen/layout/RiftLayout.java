package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.RandomState;

public interface RiftLayout {

    RiftSpace getChunkSpace(Vec3i pos, RandomState randomState);

}
