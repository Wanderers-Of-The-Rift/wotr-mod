package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.corridor.RiftSpaceCorridor;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;

import java.util.List;

public record RoomRiftSpace(Vec3i size, Vec3i center, List<RiftSpaceCorridor> corridors, RiftGeneratable template,
        TripleMirror templateTransform) implements RiftSpace {

    @Override
    public Vec3i origin() {
        return center.offset(-size.getX() / 2, -size.getY() / 2, -size.getZ() / 2);
    }

    public RoomRiftSpace offset(Vec3i pos) {
        return offset(pos.getX(), pos.getY(), pos.getZ());
    }

    public RoomRiftSpace offset(int x, int y, int z) {
        return new RoomRiftSpace(size, center.offset(x, y, z), corridors, template, templateTransform);
    }
}
