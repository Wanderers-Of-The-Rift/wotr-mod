package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.RandomState;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class RiftRoomGenerator {

    private final ConcurrentHashMap<Vec3i, WeakReference<RiftProcessedRoom>> structureCache = new ConcurrentHashMap<>();

    public Future<RiftProcessedChunk> getRoomChunk(Vec3i sectionPos, RoomRiftSpace space, ServerLevel world, RandomState randomState){
        return CompletableFuture.supplyAsync(()->getOrCreateProcessedRoom(space, world,randomState),Thread::startVirtualThread).thenApply((it)->it.getChunk(sectionPos));
    }

    public Future<RiftProcessedChunk> getAndRemoveRoomChunk(Vec3i sectionPos, RoomRiftSpace space, ServerLevel world, RandomState randomState){
        return CompletableFuture.supplyAsync(()->getOrCreateProcessedRoom(space, world,randomState),Thread::startVirtualThread).thenApply((it)->it.getAndRemoveChunk(sectionPos));
    }

    private RiftProcessedRoom getOrCreateProcessedRoom(RoomRiftSpace space, ServerLevel world, RandomState randomState) {
        var processedRoomWeak = structureCache.get(space.origin());
        RiftProcessedRoom processedRoom;
        if(processedRoomWeak!=null && ((processedRoom=processedRoomWeak.get())!=null)) return processedRoom;
        var processedRoom2 = new RiftProcessedRoom(space);
        do {
            processedRoomWeak = structureCache.compute(space.origin(), (pos, old) -> (old == null || old.refersTo(null)) ? new WeakReference<>(processedRoom2) : old);
        } while ((processedRoom=processedRoomWeak.get())==null);
        if(processedRoom==processedRoom2) {
            var origin = processedRoom2.space.origin();
            var randomFactory = randomState.getOrCreateRandomFactory(WanderersOfTheRift.id("rift"));
            var randomSource = randomFactory.at(origin.getX(),origin.getY(),origin.getZ());
            var mirror = space.templateTransform();
            if(mirror==null) {
                var mirrorInt = randomSource.nextInt(8);
                mirror = new TripleMirror(mirrorInt);
            }
            var template = space.template();
            if(template==null) {
                throw new IllegalStateException("template should not be null");
            }
            RiftGeneratable.generate(template, processedRoom2, world, new Vec3i(1, 1, 1), mirror, world.getServer(), randomSource);
            processedRoom2.markAsComplete();
        }
        return processedRoom;
    }
}
