package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.VoidRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RiftRoomGenerator {

    private final ConcurrentHashMap<Vec3i, CompletableFuture<WeakReference<RiftProcessedRoom>>> structureCache = new ConcurrentHashMap<>();
    private final PositionalRandomFactory randomFactory;
    private final List<RiftGeneratable> perimeterGeneratables;
    private final List<RiftGeneratable.JigsawProcessor> jigsawProcessors;

    public RiftRoomGenerator(PositionalRandomFactory randomFactory, List<RiftGeneratable> perimeterGeneratables,
            List<RiftGeneratable.JigsawProcessor> jigsawProcessors) {
        this.randomFactory = randomFactory;
        this.perimeterGeneratables = perimeterGeneratables;
        this.jigsawProcessors = jigsawProcessors;
    }

    public Future<RiftProcessedChunk> getAndRemoveRoomChunk(
            Vec3i sectionPos,
            RoomRiftSpace space,
            ServerLevelAccessor world) {
        return getOrCreateFutureProcessedRoom(space, world).thenApply(room -> room.getAndRemoveChunk(sectionPos));
    }

    private CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world) {
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
                    mirror = TripleMirror.random(randomSource);
                }
                var template = space.template();
                if (template == null) {
                    throw new IllegalStateException("template should not be null");
                }
                var border = new Vec3i(
                        LevelChunkSection.SECTION_WIDTH - 1
                                - ((template.size().getX() - 1) & RiftProcessedChunk.CHUNK_WIDTH_MASK),
                        LevelChunkSection.SECTION_HEIGHT - 1
                                - ((template.size().getY() - 1) & RiftProcessedChunk.CHUNK_HEIGHT_MASK),
                        LevelChunkSection.SECTION_WIDTH - 1
                                - ((template.size().getZ() - 1) & RiftProcessedChunk.CHUNK_WIDTH_MASK)
                );
                RiftGeneratable.generate(template, processedRoom2, world, border, mirror, world.getServer(),
                        randomSource, null, jigsawProcessors);
                processedRoom2.markAsComplete();
                for (var perimeter : perimeterGeneratables) {
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
                getOrCreateFutureProcessedRoom(space, world).thenAccept(newResult::complete);
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
