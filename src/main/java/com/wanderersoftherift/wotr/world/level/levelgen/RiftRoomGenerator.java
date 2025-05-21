package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RiftRoomGenerator {

    private final ConcurrentHashMap<Vec3i, CompletableFuture<WeakReference<RiftProcessedRoom>>> structureCache = new ConcurrentHashMap<>();

    public Future<RiftProcessedChunk> getAndRemoveRoomChunk(
            Vec3i sectionPos,
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory,
            RiftGeneratable perimeter) {
        return getOrCreateFutureProcessedRoom(space, world, randomFactory, perimeter)
                .thenApply(it -> it.getAndRemoveChunk(sectionPos));
    }

    private CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory,
            RiftGeneratable perimeter) {
        var newFuture = new CompletableFuture<WeakReference<RiftProcessedRoom>>();
        var processedRoomFuture = structureCache.compute(space.origin(), (key, oldFuture) -> {
            if (oldFuture == null) {
                return newFuture;
            }
            if (!oldFuture.isDone()) {
                return oldFuture;
            }
            try {
                var gn = oldFuture.get(0, TimeUnit.MICROSECONDS);
                if (gn.refersTo(null)) {
                    return newFuture;
                } else {
                    return oldFuture;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new IllegalStateException("failed to immediately get result of completed future", e);
            }
        });
        if (processedRoomFuture == newFuture) {
            return CompletableFuture.supplyAsync(() -> {
                var processedRoom2 = new RiftProcessedRoom(space);
                var origin = processedRoom2.space.origin();
                var randomSource = randomFactory.at(origin.getX(), origin.getY(), origin.getZ());
                var mirror = space.templateTransform();
                if (mirror == null) {
                    var mirrorInt = randomSource.nextInt(8);
                    mirror = new TripleMirror(mirrorInt);
                }
                var template = space.template();
                if (template == null) {
                    throw new IllegalStateException("template should not be null");
                }
                RiftGeneratable.generate(template, processedRoom2, world, new Vec3i(1, 1, 1), mirror, world.getServer(),
                        randomSource);
                processedRoom2.markAsComplete();
                if (perimeter != null) {
                    perimeter.processAndPlace(processedRoom2, world, Vec3i.ZERO, mirror);
                }
                newFuture.complete(new WeakReference<>(processedRoom2));
                return processedRoom2;
            }, Thread::startVirtualThread);
        }
        var newResult = new CompletableFuture<RiftProcessedRoom>();
        processedRoomFuture.thenAccept((weak) -> {
            var value = weak.get();
            if (value == null) {
                getOrCreateFutureProcessedRoom(space, world, randomFactory, perimeter).thenAccept(newResult::complete);
            } else {
                newResult.complete(value);
            }
        });
        return newResult;
    }

    public Future<RiftProcessedChunk> chunkOf(RiftGeneratable filler, ServerLevelAccessor world, Vec3i i) {
        var tmpRoom = new RiftProcessedRoom(new VoidRiftSpace(i));
        filler.processAndPlace(tmpRoom, world, Vec3i.ZERO, TripleMirror.NONE);
        tmpRoom.markAsComplete();
        return CompletableFuture.completedFuture(tmpRoom.getAndRemoveChunk(tmpRoom.space.origin()));
    }
}
