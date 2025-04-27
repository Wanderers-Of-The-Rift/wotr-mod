package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.List;

public class GridRiftLayout implements RiftLayout {

    @Override
    public List<RiftSpace> getChunkSpaces(ChunkPos chunkPos, RandomState randomState) {
        var gridX = chunkPos.x/3;
        var gridZ = chunkPos.z/3;
        var s = RoomRiftSpace.basicRiftSpace(new Vec3i(gridX*3, 1,gridZ*3),3,1, RoomRiftSpace.RoomType.STABLE);
        return List.of(s,s,s);
    }
}
