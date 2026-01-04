package com.wanderersoftherift.wotr.world.level.levelgen.space.corridor;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public record RiftSpaceCorridor(Vec3i position, Direction direction) {
    public Vec3i getConnectingPos(RiftSpace space) {
        return space.origin().offset(position).relative(direction);
    }
}
