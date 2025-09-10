package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.util.ShiftMath;
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

/**
 * temporary storage for blocks before they are placed in the world
 */
public class RiftProcessedRoom {

    public final RiftSpace space;
    private final AtomicReference<RiftProcessedChunk>[] chunkArray;
    private final CompletableFuture<Unit> isComplete = new CompletableFuture<>();
    private final Vec3i origin;
    private final int shiftZ;
    private final int shiftY;
    private final int shiftYZ;
    private final int xMask;
    private final int zMask;

    public RiftProcessedRoom(RiftSpace space) {
        this.space = space;
        origin = space.origin();
        shiftZ = ShiftMath.shiftForCeilPow2(space.size().getX());
        shiftY = ShiftMath.shiftForCeilPow2(space.size().getZ());
        shiftYZ = shiftZ + shiftY;
        chunkArray = new AtomicReference[space.size().getY() << shiftYZ];
        xMask = (1 << shiftZ) - 1;
        zMask = (1 << shiftY) - 1;
        for (int i = 0; i < chunkArray.length; i++) {
            if (((i & xMask) < space.size().getX()) && ((i >> shiftYZ) < space.size().getY())
                    && (((i >> shiftZ) & zMask) < space.size().getZ())) {
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
        var idx = x2 + (z2 << shiftZ) + (y2 << shiftYZ);
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
        return getOrCreateChunk(x2 + (z2 << shiftZ) + (y2 << shiftYZ));
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
        value = new RiftProcessedChunk(origin.offset(index & xMask, (index >> shiftYZ), (index >> shiftZ) & zMask),
                this);
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
        var ref = chunkArray[x2 + (z2 << shiftZ) + (y2 << shiftYZ)];
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
        var chunk = this.getOrCreateChunk(new Vec3i(position.getX() >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT,
                position.getY() >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT,
                position.getZ() >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT));
        if (chunk != null) {
            chunk.entities.add(nbt);
        }
    }

    public BlockState getBlock(int x, int y, int z) {
        var chunkX = x >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
        var chunkY = y >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT;
        var chunkZ = z >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
        var chunk = getChunk(chunkX, chunkY, chunkZ);
        if (chunk == null) {
            return null;
        }
        return chunk.getBlockStatePure(x & RiftProcessedChunk.CHUNK_WIDTH_MASK,
                y & RiftProcessedChunk.CHUNK_HEIGHT_MASK, z & RiftProcessedChunk.CHUNK_WIDTH_MASK);
    }

    public boolean getMerged(int x, int y, int z) {
        var chunkX = x >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
        var chunkY = y >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT;
        var chunkZ = z >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
        var chunk = getChunk(chunkX, chunkY, chunkZ);
        if (chunk == null) {
            return false;
        }
        var index = ((y & RiftProcessedChunk.CHUNK_HEIGHT_MASK) << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)
                | (z & RiftProcessedChunk.CHUNK_WIDTH_MASK);
        var shift = x & RiftProcessedChunk.CHUNK_WIDTH_MASK;
        return ((chunk.hidden[index] >> shift) & 1) != 0 || ((chunk.newlyAdded[index] >> shift) & 1) == 0;
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
        var chunkX = x >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
        var chunkY = y >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT;
        var chunkZ = z >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
        var chunk = getOrCreateChunk(chunkX, chunkY, chunkZ);
        chunk.setBlockStatePure(x & RiftProcessedChunk.CHUNK_WIDTH_MASK, y & RiftProcessedChunk.CHUNK_HEIGHT_MASK,
                z & RiftProcessedChunk.CHUNK_WIDTH_MASK, state);
    }

    public void setBlock(Vec3i basePos, BlockState blockState) {
        setBlock(basePos.getX(), basePos.getY(), basePos.getZ(), blockState);
    }

    public List<RiftProcessedChunk> getOrCreateAllChunks() {
        var result = new ArrayList<RiftProcessedChunk>();
        for (int i = 0; i < chunkArray.length; i++) {
            var chunk = getOrCreateChunk(i);
            if (chunk != null) {
                result.add(chunk);
            }
        }
        return result;
    }
}
