package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftTemplates;
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
            var mirrorInt = randomSource.nextInt(8);
            var actualSize = processedRoom2.space.size().multiply(16).subtract(new Vec3i(1,1,1));
            var template = RiftTemplates.random(world.getServer(), WanderersOfTheRift.id("rift/room_"+space.type().toString().toLowerCase()),((mirrorInt & 0b100) !=0? TripleMirror.DIAGONAL : TripleMirror.NONE).applyToPosition(actualSize,0,0),randomSource);//todo move template choice to layout

            if(template!=null) {
                RiftGeneratable.generate(template, processedRoom2, world, new Vec3i(1, 1, 1), new TripleMirror(mirrorInt), world.getServer(), randomSource);
            }
            processedRoom2.markAsComplete();
        }
        return processedRoom;
    }
}
