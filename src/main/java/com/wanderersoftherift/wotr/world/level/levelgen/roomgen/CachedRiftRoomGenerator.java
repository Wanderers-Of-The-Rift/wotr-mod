package com.wanderersoftherift.wotr.world.level.levelgen.roomgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftConfig;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratableId;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public record CachedRiftRoomGenerator(
        ConcurrentHashMap<Vec3i, CompletableFuture<WeakReference<RiftProcessedRoom>>> cache,
        RiftRoomGenerator baseGenerator) implements RiftRoomGenerator {

    public CachedRiftRoomGenerator(RiftRoomGenerator extraRiftRoomGenerator) {
        this(new ConcurrentHashMap<>(), extraRiftRoomGenerator);
    }

    @Override
    public CompletableFuture<RiftProcessedRoom> getOrCreateFutureProcessedRoom(
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory) {
        var newFuture = new CompletableFuture<WeakReference<RiftProcessedRoom>>();
        var processedRoomFuture = cache.compute(space.origin(), (key, oldFuture) -> {
            if (oldFuture == null) {
                return newFuture;
            }
            if (!oldFuture.isDone()) {
                return oldFuture;
            }
            try {
                var futureNow = oldFuture.get(0, TimeUnit.MICROSECONDS);
                if (futureNow.refersTo(null)) {
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
                var newRoom = baseGenerator.getOrCreateFutureProcessedRoom(space, world, randomFactory);
                RiftProcessedRoom processedRoom2 = null;
                try {
                    processedRoom2 = newRoom.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                newFuture.complete(new CacheEntry(processedRoom2, processedRoom2.space.origin(), cache));
                return processedRoom2;
            }, Thread::startVirtualThread);
        }
        var newResult = new CompletableFuture<RiftProcessedRoom>();
        processedRoomFuture.thenAccept((weak) -> {
            var value = weak.get();
            if (value == null) {
                getOrCreateFutureProcessedRoom(space, world, randomFactory).thenAccept(newResult::complete);
            } else {
                newResult.complete(value);
            }
        });
        return newResult;
    }

    @Override
    public Object2IntMap<RiftGeneratableId> getGeneratableCounts(
            RoomRiftSpace space,
            ServerLevelAccessor world,
            PositionalRandomFactory randomFactory) {
        // TODO: cache
        return baseGenerator.getGeneratableCounts(space, world, randomFactory);
    }

    public record Factory(RiftRoomGenerator.Factory baseFactory) implements RiftRoomGenerator.Factory {
        public static final MapCodec<Factory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(RiftRoomGenerator.Factory.CODEC.fieldOf("base").forGetter(Factory::baseFactory))
                .apply(instance, Factory::new));

        @Override
        public RiftRoomGenerator create(RiftConfig jigsawListProcessors) {
            return new CachedRiftRoomGenerator(baseFactory.create(jigsawListProcessors));
        }

        @Override
        public MapCodec<? extends RiftRoomGenerator.Factory> codec() {
            return CODEC;
        }
    }

    private class CacheEntry extends WeakReference<RiftProcessedRoom> {

        private static final ReferenceQueue<RiftProcessedRoom> QUEUE = new ReferenceQueue<>();

        private static final Thread GC_THREAD = Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Reference<? extends RiftProcessedRoom> ref = QUEUE.remove(0);
                    if (ref instanceof CacheEntry cacheEntry) {
                        cacheEntry.cache.remove(cacheEntry.position);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        private final Vec3i position;
        private final ConcurrentHashMap<Vec3i, CompletableFuture<WeakReference<RiftProcessedRoom>>> cache;

        public CacheEntry(RiftProcessedRoom referent, Vec3i position,
                ConcurrentHashMap<Vec3i, CompletableFuture<WeakReference<RiftProcessedRoom>>> cache) {
            super(referent, QUEUE);
            this.position = position;
            this.cache = cache;
        }
    }
}
