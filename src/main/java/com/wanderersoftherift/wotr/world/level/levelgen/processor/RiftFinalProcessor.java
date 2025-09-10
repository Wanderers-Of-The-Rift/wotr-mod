package com.wanderersoftherift.wotr.world.level.levelgen.processor;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;

public interface RiftFinalProcessor {
    void finalizeRoomProcessing(
            RiftProcessedRoom room,
            ServerLevelAccessor world,
            BlockPos structurePos,
            Vec3i pieceSize);
}
