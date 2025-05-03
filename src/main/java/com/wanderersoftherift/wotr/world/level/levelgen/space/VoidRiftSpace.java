package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
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

    @Override
    public TripleMirror templateTransform() {
        return TripleMirror.NONE;
    }

    @Override
    public RiftGeneratable template() {
        return null;
    }

}
