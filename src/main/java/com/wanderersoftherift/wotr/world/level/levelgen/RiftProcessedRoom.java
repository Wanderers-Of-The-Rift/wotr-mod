package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class RiftProcessedRoom {

    public final RiftSpace space;
    private final AtomicReference<RiftProcessedChunk>[] chunkArray = new AtomicReference[64];
    private final CompletableFuture<Unit> isComplete = new CompletableFuture<>();
    private final Vec3i origin;

    public RiftProcessedRoom(RiftSpace space) {
        this.space = space;
        origin = space.origin();
        for (int i = 0; i < chunkArray.length; i++) {
            if ((i & 0b11) < space.size().getX() && ((i >> 4) & 0b11) < space.size().getY()
                    && ((i >> 2) & 0b11) < space.size().getZ()) {
                chunkArray[i] = new AtomicReference<>();
            }
        }
    }

    public RiftProcessedChunk getAndRemoveChunk(Vec3i sectionPos) {
        try {
            isComplete.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        var x2 = sectionPos.getX() - origin.getX();
        var y2 = sectionPos.getY() - origin.getY();
        var z2 = sectionPos.getZ() - origin.getZ();
        if (x2 < 0 || x2 >= space.size().getX() || y2 < 0 || y2 >= space.size().getY() || z2 < 0
                || z2 >= space.size().getZ()) {
            return null;
        }
        var idx = x2 + (z2 << 2) + (y2 << 4);
        var ref = chunkArray[idx];
        if (ref == null) {
            return null;
        }
        chunkArray[idx] = null;
        return ref.get();
    }

    public RiftProcessedChunk getOrCreateChunk(Vec3i sectionPos) {
        return getOrCreateChunk(sectionPos.getX(), sectionPos.getY(), sectionPos.getZ());
    }

    public RiftProcessedChunk getOrCreateChunk(int x, int y, int z) {
        var x2 = x - origin.getX();
        var y2 = y - origin.getY();
        var z2 = z - origin.getZ();
        if (x2 < 0 || x2 >= space.size().getX() || y2 < 0 || y2 >= space.size().getY() || z2 < 0
                || z2 >= space.size().getZ()) {
            return null;
        }
        return getOrCreateChunk(x2 + (z2 << 2) + (y2 << 4));
    }

    private RiftProcessedChunk getOrCreateChunk(int index) {
        var ref = chunkArray[index];
        if (ref == null) {
            return null;
        }
        var value = ref.get();
        if (value != null) {
            return value;
        }
        value = new RiftProcessedChunk(origin.offset(index & 0b11, (index >> 4) & 0b11, (index >> 2) & 0b11), this);
        if (ref.compareAndSet(null, value)) {
            return value;
        }
        return ref.get();
    }

    public RiftProcessedChunk getChunk(int x, int y, int z) {
        var x2 = x - origin.getX();
        var y2 = y - origin.getY();
        var z2 = z - origin.getZ();
        if (x2 < 0 || x2 >= space.size().getX() || y2 < 0 || y2 >= space.size().getY() || z2 < 0
                || z2 >= space.size().getZ()) {
            return null;
        }
        var ref = chunkArray[x2 + (z2 << 2) + (y2 << 4)];
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    public void markAsComplete() {
        isComplete.complete(Unit.INSTANCE);
    }

    public void addEntity(StructureTemplate.StructureEntityInfo info) {
        var position = info.blockPos;
        var nbt = info.nbt;
        if (nbt.getAllKeys().contains("TileX") && nbt.getAllKeys().contains("TileY")
                && nbt.getAllKeys().contains("TileZ")) {
            if (position.getX() < 0) {
                position = new BlockPos(position.getX() - 1, position.getY(), position.getZ());
            }
            if (position.getZ() < 0) {
                position = new BlockPos(position.getX(), position.getY(), position.getZ() - 1);
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
        this.getOrCreateChunk(new Vec3i(position.getX() >> 4, position.getY() >> 4, position.getZ() >> 4)).entities
                .add(nbt);
    }

    public BlockState getBlock(int x, int y, int z) {
        var chunkX = x >> 4;
        var chunkY = y >> 4;
        var chunkZ = z >> 4;
        var chunk = getChunk(chunkX, chunkY, chunkZ);
        if (chunk == null) {
            return null;
        }
        return chunk.getBlockStatePure(x & 0xf, y & 0xf, z & 0xf);
    }

    public boolean getMerged(int x, int y, int z) {
        var chunkX = x >> 4;
        var chunkY = y >> 4;
        var chunkZ = z >> 4;
        var chunk = getChunk(chunkX, chunkY, chunkZ);
        if (chunk == null) {
            return false;
        }
        return ((chunk.hidden[((y & 0xf) << 4) | (z & 0xf)] >> (x & 0xf)) & 1) != 0
                || ((chunk.newlyAdded[((y & 0xf) << 4) | (z & 0xf)] >> (x & 0xf)) & 1) == 0;
    }

    public void clearNewFlags() {
        for (var chunkAtomic : chunkArray) {
            if (chunkAtomic == null) {
                continue;
            }
            var chunk = chunkAtomic.get();
            if (chunk == null) {
                continue;
            }
            Arrays.fill(chunk.newlyAdded, (short) 0);
        }
    }

    public BlockState getBlock(Vec3i pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setBlock(int x, int y, int z, BlockState state) {
        var chunkX = x >> 4;
        var chunkY = y >> 4;
        var chunkZ = z >> 4;
        var chunk = getOrCreateChunk(chunkX, chunkY, chunkZ);
        chunk.setBlockStatePure(x & 0xf, y & 0xf, z & 0xf, state);
    }

    public void setBlock(Vec3i basePos, BlockState blockState) {
        setBlock(basePos.getX(), basePos.getY(), basePos.getZ(), blockState);
    }

    public List<RiftProcessedChunk> getOrCreateAllChunks() {
        var result = new ArrayList<RiftProcessedChunk>();
        for (int i = 0; i < 64; i++) {
            var chunk = getOrCreateChunk(i);
            if (chunk != null) {
                result.add(chunk);
            }
        }
        return result;
    }
}
