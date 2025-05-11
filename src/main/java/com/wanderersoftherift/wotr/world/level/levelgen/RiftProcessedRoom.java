package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

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

    public void addEntity(StructureTemplate.StructureEntityInfo info) {
        var position = info.blockPos;
        var nbt = info.nbt;
        if(nbt.getAllKeys().contains("TileX") && nbt.getAllKeys().contains("TileY") && nbt.getAllKeys().contains("TileZ")){
            if (position.getX()<0){
                position = new BlockPos(position.getX()-1, position.getY(), position.getZ());
            }
            if (position.getZ()<0){
                position = new BlockPos(position.getX(), position.getY(), position.getZ()-1);
            }
            nbt.putInt("TileX", position.getX());
            nbt.putInt("TileY", position.getY());
            nbt.putInt("TileZ", position.getZ());
        }
        ListTag listtag = new ListTag();
        listtag.add(DoubleTag.valueOf(info.pos.x));
        listtag.add(DoubleTag.valueOf(info.pos.y));
        listtag.add(DoubleTag.valueOf(info.pos.z));
        nbt.put("Pos", listtag);
        nbt.remove("UUID");
        this.getOrCreateChunk(new Vec3i(position.getX() >> 4, position.getY() >> 4, position.getZ() >> 4)).entities.add(nbt);
    }

    public BlockState getBlock(int x, int y, int z) {
        var chunkX = x >> 4;
        var chunkY = y >> 4;
        var chunkZ = z >> 4;
        var chunk = data.get(new Vec3i(chunkX, chunkY, chunkZ));
        if(chunk==null){
            return null;
        }
        return chunk.getBlockStatePure(x & 0xf, y & 0xf, z & 0xf);
    }

    public BlockState getBlock(Vec3i pos){
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }


    public void setBlock(int x, int y, int z, BlockState state) {
        var chunkX = x >> 4;
        var chunkY = y >> 4;
        var chunkZ = z >> 4;
        var chunk = getOrCreateChunk(new Vec3i(chunkX, chunkY, chunkZ));
        chunk.setBlockStatePure(x & 0xf, y & 0xf, z & 0xf, state);
    }

    public void setBlock(Vec3i basePos, BlockState blockState) {
        setBlock(basePos.getX(), basePos.getY(), basePos.getZ(), blockState);
    }
}
