package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.mixin.AccessorPalettedContainer;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class RiftProcessedChunk {

    public final Vec3i origin;
    public final BlockState[] blocks = new BlockState[4096];
    public final short[] hidden = new short[256];
    public final short[] newlyAdded = new short[256];
    public final RiftProcessedRoom parentRoom;
    public final ArrayList<CompoundTag> entities = new ArrayList<>();
    public final ArrayList<BlockEntity> blockEntities = new ArrayList<BlockEntity>(4096);

    public RiftProcessedChunk(Vec3i origin, RiftProcessedRoom parentRoom) {
        this.origin = origin;
        this.parentRoom = parentRoom;
    }

    public void placeInWorld(ChunkAccess chunk, ServerLevelAccessor level) {
        var mutablePosition = new BlockPos.MutableBlockPos();
        swapMinecraftSection(chunk.getSections(), origin.getY() - chunk.getMinY() / 16);
        for (int idx = 0; idx < blockEntities.size(); idx++) {

            var entity = blockEntities.get(idx);
            if (entity != null && level != null) {
                chunk.setBlockEntity(entity);
            }
        }
        for (int idx = 0; idx < entities.size(); idx++) {
            var entityNBT = entities.get(idx);
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
    public void swapMinecraftSection(LevelChunkSection[] sectionArray, int sectionIndex) {
        var air = Blocks.AIR.defaultBlockState();
        var oldSection = sectionArray[sectionIndex];
        var size = 0;
        var uniqueStatesList = new BlockState[4096];
        var uniqueStatesHashTable = new BlockState[64];
        var uniqueStatesIndexHashTable = new int[64];
        var uniqueStatesFallback = new IdentityHashMap<BlockState, Integer>();
        for (var state : blocks) {
            var actualState = state == null ? air : state;
            var idx = System.identityHashCode(actualState) * FibonacciHashing.GOLDEN_RATIO_INT >>> 26;
            var state2 = uniqueStatesHashTable[idx];
            if (state2 == null) {
                uniqueStatesHashTable[idx] = actualState;
                uniqueStatesList[size] = actualState;
                uniqueStatesIndexHashTable[idx] = size++;
            } else if (state2 != actualState) {
                uniqueStatesList[size] = actualState;
                uniqueStatesFallback.putIfAbsent(actualState, size++);
            }
        }

        if (size == 0) {
            return;
        }
        var bits = 32 - Integer.numberOfLeadingZeros(size - 1);

        var reg = ((AccessorPalettedContainer) oldSection.getStates()).getRegistry();
        if (bits > 8) {
            bits = 32 - Integer.numberOfLeadingZeros(reg.size() - 1);
            var shift = (32 - Integer.numberOfLeadingZeros(bits - 1));
            var bitsPowerOfTwo = 1 << shift;

            var strat = PalettedContainer.Strategy.SECTION_STATES;
            var config = strat.<BlockState>getConfiguration(reg, bitsPowerOfTwo);

            var longs = new long[4096 / (64 >> shift)];

            var valuesPerLong = 64 >> shift;
            for (int i = 0; i < longs.length; i++) {
                var value = 0L;
                for (int j = valuesPerLong - 1; j >= 0; j--) {
                    value <<= bitsPowerOfTwo;
                    var idx = i * valuesPerLong + j;
                    var state = blocks[idx];
                    if (state == null) {
                        state = air;
                    }
                    value |= reg.getId(state);

                }
                longs[i] = value;
            }

            var storage = new SimpleBitStorage(bitsPowerOfTwo, 4096, longs);

            var stateList = new ArrayList<BlockState>(size);
            for (int i = 0; i < size; i++) {
                stateList.add(uniqueStatesList[i]);
            }

            var newPalettedContainer = new PalettedContainer<BlockState>(reg, strat, config, storage, stateList);
            var newSection = new LevelChunkSection(newPalettedContainer, oldSection.getBiomes());
            sectionArray[sectionIndex] = newSection;

        } else if (bits != 0) {
            var shift = Integer.max(32 - Integer.numberOfLeadingZeros(bits - 1), 2);
            var bitsPowerOfTwo = 1 << shift;

            var strat = PalettedContainer.Strategy.SECTION_STATES;
            var config = strat.<BlockState>getConfiguration(reg, bitsPowerOfTwo);

            var longs = new long[4096 / (64 >> shift)];

            var valuesPerLong = 64 >> shift;
            for (int i = 0; i < longs.length; i++) {
                var value = 0L;
                for (int j = valuesPerLong - 1; j >= 0; j--) {
                    value <<= bitsPowerOfTwo;
                    var idx = i * valuesPerLong + j;
                    var state = blocks[idx];
                    if (state == null) {
                        state = air;
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

            var stateList = new ArrayList<BlockState>(size);
            for (int i = 0; i < size; i++) {
                stateList.add(uniqueStatesList[i]);
            }

            var newPalettedContainer = new PalettedContainer<BlockState>(reg, strat, config, storage, stateList);
            var newSection = new LevelChunkSection(newPalettedContainer, oldSection.getBiomes());
            sectionArray[sectionIndex] = newSection;

        } else {

            var strat = PalettedContainer.Strategy.SECTION_STATES;
            var config = strat.<BlockState>getConfiguration(reg, 0);
            var storage = new ZeroBitStorage(4096);
            var newPalettedContainer = new PalettedContainer<BlockState>(reg, strat, config, storage,
                    List.of(uniqueStatesList[0]));
            var newSection = new LevelChunkSection(newPalettedContainer, oldSection.getBiomes());
            sectionArray[sectionIndex] = newSection;
            // var palette = new SingleValuePalette<BlockState>()
        }
    }
}
