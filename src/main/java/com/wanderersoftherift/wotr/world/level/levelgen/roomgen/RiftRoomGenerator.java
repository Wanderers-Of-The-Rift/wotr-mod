package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface RiftRoomGenerator {

    default Future<RiftProcessedChunk> getAndRemoveRoomChunk(
            Vec3i sectionPos,
            RoomRiftSpace space,
            ServerLevelAccessor world) {
        return getOrCreateFutureProcessedRoom(space, world).thenApply(room -> room.getAndRemoveChunk(sectionPos));
    }

    CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(RoomRiftSpace space, ServerLevelAccessor world);

    static Future<RiftProcessedChunk> chunkOf(RiftGeneratable filler, ServerLevelAccessor world, Vec3i i) {
        var tmpRoom = new RiftProcessedRoom(new VoidRiftSpace(i));
        filler.processAndPlace(tmpRoom, world, Vec3i.ZERO, TripleMirror.NONE);
        tmpRoom.markAsComplete();
        return CompletableFuture.completedFuture(tmpRoom.getAndRemoveChunk(tmpRoom.space.origin()));
    }
}
