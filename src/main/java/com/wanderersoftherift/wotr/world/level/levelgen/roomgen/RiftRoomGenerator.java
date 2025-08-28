package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

public interface RiftRoomGenerator {

    default Future<RiftProcessedChunk> getAndRemoveRoomChunk(
            Vec3i sectionPos,
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory) {
        return getOrCreateFutureProcessedRoom(space, world, randomFactory)
                .thenApply(room -> room.getAndRemoveChunk(sectionPos));
    }

    CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory);

    static Future<RiftProcessedChunk> chunkOf(RiftGeneratable filler, ServerLevelAccessor world, Vec3i i) {
        var tmpRoom = new RiftProcessedRoom(new VoidRiftSpace(i));
        filler.processAndPlace(tmpRoom, world, Vec3i.ZERO, TripleMirror.NONE);
        tmpRoom.markAsComplete();
        return CompletableFuture.completedFuture(tmpRoom.getAndRemoveChunk(tmpRoom.space.origin()));
    }

    interface Factory {
        Codec<Factory> CODEC = WotrRegistries.RIFT_ROOM_GENERATOR_FACTORY_TYPES.byNameCodec()
                .dispatch(Factory::codec, Function.identity());

        RiftRoomGenerator create(RiftConfig config);

        MapCodec<? extends Factory> codec();
    }
}
