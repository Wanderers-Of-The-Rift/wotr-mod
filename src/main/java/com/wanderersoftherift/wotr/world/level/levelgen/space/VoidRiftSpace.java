package com.wanderersoftherift.wotr.world.level.levelgen.space;

import net.minecraft.core.Vec3i;

import java.util.List;

public class VoidRiftSpace implements RiftSpace {
    @Override
    public Vec3i origin() {
        return null;//todo?
    }

    @Override
    public Vec3i size() {
        return new Vec3i(1,1,1);
    }

    @Override
    public List<RiftSpaceCorridor> corridors() {
        return List.of();
    }

}
