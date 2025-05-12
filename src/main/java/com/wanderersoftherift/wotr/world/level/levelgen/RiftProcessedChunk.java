package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.util.FibonacciHashing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

import java.util.ArrayList;
import java.util.IdentityHashMap;

public class RiftProcessedChunk {

    public final Vec3i origin;
    public final BlockState[] blocks = new BlockState[4096];
    public final CompoundTag[] blockNBT = new CompoundTag[4096];
    public final RiftProcessedRoom parentRoom;
    public final ArrayList<CompoundTag> entities = new ArrayList<>();

    public RiftProcessedChunk(Vec3i origin, RiftProcessedRoom parentRoom) {
        this.origin = origin;
        this.parentRoom = parentRoom;
    }

    public void placeInWorld(ChunkAccess chunk, ServerLevelAccessor level) {
        var mutablePosition = new BlockPos.MutableBlockPos();
        swapDataInMinecraftSection(chunk.getSection((origin.getY() - chunk.getMinY()) / 16));
        for (int index = 0; index < 4096; index++) {
            var block = blocks[index];
            if (block == null) {
                continue;
            }
            var x = index & 0xf;
            var z = (index >> 4) & 0xf;
            var y = (index >> 8) & 0xf;
            mutablePosition.set(x, y + 16 * origin.getY(), z);
            chunk.setBlockState(mutablePosition, block, false);
            var nbt = blockNBT[index];
            if (block.hasBlockEntity() && nbt != null && level != null) {
                nbt.putInt("x", x | (origin.getX() << 4));
                nbt.putInt("y", y | (origin.getY() << 4));
                nbt.putInt("z", z | (origin.getZ() << 4));
                chunk.setBlockEntityNbt(nbt);
                level.getBlockEntity(mutablePosition.move((origin.getX() << 4), 0, (origin.getZ() << 4)));
            }
        }
        for (int i = 0; i < entities.size(); i++) {
            var entityNBT = entities.get(i);
            EntityType.create(entityNBT, level.getLevel(), EntitySpawnReason.STRUCTURE).ifPresent((entity) -> {

                if (entity instanceof Mob mob) {
                    mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(entity.position())),
                            EntitySpawnReason.STRUCTURE, (SpawnGroupData) null);
                }
                level.addFreshEntityWithPassengers(entity);
            });
        }
    }

    public void setBlockState(Vec3i position, BlockState blockState) {
        setBlockState(position.getX(), position.getY(), position.getZ(), blockState);
    }

    public void setBlockState(int x, int y, int z, BlockState blockState) {
        var index = x + z * 16 + (y - origin.getY() * 16) * 256;
        blocks[index] = blockState;
    }

    public void setBlockStatePure(int x, int y, int z, BlockState blockState) {
        var index = x + z * 16 + y * 256;
        blocks[index] = blockState;
    }

    public BlockState getBlockState(Vec3i position) {
        return getBlockState(position.getX(), position.getY(), position.getZ());
    }

    public BlockState getBlockState(int x, int y, int z) {
        var index = x + z * 16 + (y - origin.getY() * 16) * 256;
        return blocks[index];
    }

    public BlockState getBlockStatePure(int x, int y, int z) {
        var index = x + z * 16 + y * 256;
        return blocks[index];
    }

    /**
     * this should at some point replace placeInWorld
     */
    public void swapDataInMinecraftSection(LevelChunkSection minecraftSection) {

        var size = 0;
        var uniqueStatesHashTable = new BlockState[64];
        var uniqueStatesIndexHashTable = new int[64];
        var uniqueStatesFallback = new IdentityHashMap<BlockState, Integer>();
        for (var state : blocks) {
            var idx = System.identityHashCode(state) * FibonacciHashing.GOLDEN_RATIO_INT >>> 26;
            var state2 = uniqueStatesHashTable[idx];
            if (state2 == null) {
                uniqueStatesHashTable[idx] = state;
                uniqueStatesIndexHashTable[idx] = size++;
            } else if (state2 != state) {
                uniqueStatesFallback.putIfAbsent(state, size++);
            }
        }

        if (size == 0) {
            return;
        }
        var bits = 32 - Integer.numberOfLeadingZeros(size - 1);

        if (bits != 0) {
            var shift = (32 - Integer.numberOfLeadingZeros(bits - 1));
            var bitsPowerOfTwo = 1 << shift;

            var strat = PalettedContainer.Strategy.SECTION_STATES;
            // var config = strat.<BlockState>getConfiguration(new , bitsPowerOfTwo);

            var longs = new long[4096 / (64 >> shift)];

            var valuesPerLong = 64 >> shift;
            for (int i = 0; i < longs.length; i++) {
                var value = 0L;
                for (int j = valuesPerLong - 1; j >= 0; j--) {
                    value <<= bitsPowerOfTwo;
                    var idx = i * valuesPerLong + j;
                    var state = blocks[idx];
                    if (state == null) {
                        continue;
                    }
                    var uniqueIdx = System.identityHashCode(state) * FibonacciHashing.GOLDEN_RATIO_INT >>> 26;
                    var state2 = uniqueStatesHashTable[uniqueIdx];
                    if (state2 == null) {
                        // should not be possible
                    } else if (state2 != state) {
                        value |= uniqueStatesFallback.get(state);
                    } else {
                        value |= uniqueStatesIndexHashTable[uniqueIdx];
                    }
                }
                longs[i] = value;
            }

            var storage = new SimpleBitStorage(bitsPowerOfTwo, 4096, longs);

        } else {

            var strat = PalettedContainer.Strategy.SECTION_STATES;
            var storage = new ZeroBitStorage(4096);
        }
    }
}
