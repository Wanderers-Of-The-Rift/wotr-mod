package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.List;

public interface RiftLayout {

    List<RiftSpace> getChunkSpaces(ChunkPos access, RandomState randomState);

}
