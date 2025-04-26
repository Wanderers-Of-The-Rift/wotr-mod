package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Unit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class RiftProcessedRoom {

    private final ConcurrentHashMap<Vec3i, RiftProcessedChunk> data = new ConcurrentHashMap<>();
    private final CompletableFuture<Unit> isComplete = new CompletableFuture<>();
    public final RoomRiftSpace space;

    public RiftProcessedRoom(RoomRiftSpace space) {
        this.space = space;
    }


    public RiftProcessedChunk getChunk(Vec3i sectionPos) {
        try {
            isComplete.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return data.get(sectionPos);
    }

    public RiftProcessedChunk getAndRemoveChunk(Vec3i sectionPos) {
        try {
            isComplete.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return data.remove(sectionPos);
    }


    public RiftProcessedChunk getOrCreateChunk(Vec3i sectionPos) {
        return data.computeIfAbsent(sectionPos, (pos)->new RiftProcessedChunk(sectionPos,this));
    }

    public void markAsComplete() {
        isComplete.complete(Unit.INSTANCE);
    }
}
