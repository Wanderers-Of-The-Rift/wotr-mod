package com.wanderersoftherift.wotr.world.level.levelgen.layout.shape;

import net.minecraft.core.Vec3i;

public interface FiniteRiftShape extends RiftShape {

    Vec3i getBoxStart();

    Vec3i getBoxSize();

    @Override
    default boolean isPositionValid(int x, int y, int z) {
        var start = getBoxStart();
        var end = start.offset(getBoxSize());
        return RiftShape.super.isPositionValid(x, y, z) && end.getX() > x && start.getX() <= x && end.getY() > y
                && start.getY() <= y && end.getZ() > z && start.getZ() <= z;
    }
}
