package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.concurrent.CompletableFuture;

public record ExtraRiftRoomGenerator(RiftGeneratable generatable, RiftRoomGenerator baseGenerator)
        implements RiftRoomGenerator {
    @Override
    public CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world) {
        return baseGenerator.getOrCreateFutureProcessedRoom(space, world).thenApply(room -> {
            var mirror = space.templateTransform();
            generatable.processAndPlace(room, world, Vec3i.ZERO, mirror);
            return room;
        });
    }
}
