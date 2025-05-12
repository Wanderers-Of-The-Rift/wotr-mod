package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.RandomState;

public class GridRiftLayout implements RiftLayout {

    @Override
    public RiftSpace getChunkSpace(Vec3i chunkPos, RandomState randomState) {
        var gridX = chunkPos.getX() / 3;
        var gridZ = chunkPos.getZ() / 3;
        var s = RoomRiftSpace.basicRiftSpace(new Vec3i(gridX * 3, 1, gridZ * 3), 3, 1, RoomRiftSpace.RoomType.STABLE);
        if (chunkPos.getY() >= -1 && chunkPos.getY() <= 1) {
            return s;
        } else {
            return null;
        }
    }
}
