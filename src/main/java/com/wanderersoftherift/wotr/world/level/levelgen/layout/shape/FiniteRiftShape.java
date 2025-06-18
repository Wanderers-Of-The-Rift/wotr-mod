package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import net.minecraft.core.Vec3i;

public interface FiniteRiftShape extends RiftShape {

    Vec3i getBoxStart();

    Vec3i getBoxSize();
}
