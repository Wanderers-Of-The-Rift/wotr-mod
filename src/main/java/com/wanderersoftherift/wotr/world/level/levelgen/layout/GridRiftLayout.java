package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

@Deprecated
public class GridRiftLayout implements RiftLayout {

    @Override
    public RiftSpace getChunkSpace(Vec3i chunkPos) {
        var gridX = chunkPos.getX() / 3;
        var gridZ = chunkPos.getZ() / 3;
        // space would have to be provided by RoomRandomizer for this to work properly
        var s = RoomRiftSpace.basicRiftSpace(new Vec3i(gridX * 3, 1, gridZ * 3), 3, 1);
        if (chunkPos.getY() >= -1 && chunkPos.getY() <= 1) {
            return s;
        } else {
            return null;
        }
    }

    @Override
    public boolean validateCorridor(int x, int y, int z, Direction d) {
        return true;
    }
}
