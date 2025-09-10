package com.wanderersoftherift.wotr.world.level.levelgen;

import com.wanderersoftherift.wotr.mixin.AccessorPalettedContainer;
import com.wanderersoftherift.wotr.util.FibonacciHashing;
import com.wanderersoftherift.wotr.util.ShiftMath;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
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
import java.util.List;

/**
 * temporary storage for blocks before they are placed in the world
 */
public class RiftProcessedChunk {

    public static final int CHUNK_WIDTH_SHIFT = ShiftMath.shiftForCeilPow2(LevelChunkSection.SECTION_WIDTH);
    public static final int CHUNK_WIDTH_SHIFT_2 = ShiftMath.shiftForCeilPow2(LevelChunkSection.SECTION_WIDTH) << 1;
    public static final int CHUNK_HEIGHT_SHIFT = ShiftMath.shiftForCeilPow2(LevelChunkSection.SECTION_HEIGHT);
    public static final int CHUNK_WIDTH_MASK = (1 << CHUNK_WIDTH_SHIFT) - 1;
    public static final int CHUNK_HEIGHT_MASK = (1 << CHUNK_HEIGHT_SHIFT) - 1;
    public final Vec3i origin;
    public final BlockState[] blocks = new BlockState[LevelChunkSection.SECTION_SIZE];
    public final short[] hidden = new short[1 << CHUNK_WIDTH_SHIFT_2];
    public final short[] newlyAdded = new short[1 << CHUNK_WIDTH_SHIFT_2];
    public final RiftProcessedRoom parentRoom;
    public final ArrayList<CompoundTag> entities = new ArrayList<>();
    public final ArrayList<BlockEntity> blockEntities = new ArrayList<>(LevelChunkSection.SECTION_SIZE);

    public RiftProcessedChunk(Vec3i origin, RiftProcessedRoom parentRoom) {
        this.origin = origin;
        this.parentRoom = parentRoom;
    }

    public void placeInWorld(ChunkAccess chunk, ServerLevelAccessor level) {
        swapMinecraftSection(chunk.getSections(), origin.getY() - (chunk.getMinY() >> CHUNK_HEIGHT_SHIFT));
        for (int idx = 0; idx < blockEntities.size(); idx++) {

            var entity = blockEntities.get(idx);
            var pos = entity.getBlockPos();
            if (entity != null && level != null && entity.isValidBlockState(
                    getBlockState(pos.getX() & CHUNK_WIDTH_MASK, pos.getY(), pos.getZ() & CHUNK_WIDTH_MASK))) {
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
        var index = x + (z << CHUNK_WIDTH_SHIFT) + ((y - (origin.getY() << CHUNK_HEIGHT_SHIFT)) << CHUNK_WIDTH_SHIFT_2);
        blocks[index] = blockState;
    }

    public void setBlockStatePure(int x, int y, int z, BlockState blockState) {
        var index = x + (z << CHUNK_WIDTH_SHIFT) + (y << CHUNK_WIDTH_SHIFT_2);
        blocks[index] = blockState;
    }

    public BlockState getBlockState(Vec3i position) {
        return getBlockState(position.getX(), position.getY(), position.getZ());
    }

    public BlockState getBlockState(int x, int y, int z) {
        var index = x + (z << CHUNK_WIDTH_SHIFT) + ((y - (origin.getY() << CHUNK_HEIGHT_SHIFT)) << CHUNK_WIDTH_SHIFT_2);
        return blocks[index];
    }

    public BlockState getBlockStatePure(int x, int y, int z) {
        var index = x + (z << CHUNK_WIDTH_SHIFT) + (y << CHUNK_WIDTH_SHIFT_2);
        return blocks[index];
    }

    /**
     * turns this chunk into LevelSection, then swaps it with existing section in the world
     */
    public void swapMinecraftSection(LevelChunkSection[] sectionArray, int sectionIndex) {
        var air = Blocks.AIR.defaultBlockState();
        var oldSection = sectionArray[sectionIndex];
        var size = 0;
        // var uniqueStatesList = new BlockState[LevelChunkSection.SECTION_SIZE];
        var uniqueStatesList = new BlockState[257]; // no need to check more than that, if there is so many unique
                                                    // states, registry is used
        var uniqueStatesHashTable = new BlockState[64];
        var uniqueStatesIndexHashTable = new int[64];
        var uniqueStatesFallback = new Reference2IntOpenHashMap<BlockState>();
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
            if (size > 256) {
                break;
            }
        }

        if (size == 0) {
            return;
        }
        var bits = ShiftMath.shiftForCeilPow2(size);

        var registry = ((AccessorPalettedContainer) oldSection.getStates()).getRegistry();
        var strat = PalettedContainer.Strategy.SECTION_STATES;
        if (bits == 0) {
            var config = strat.<BlockState>getConfiguration(registry, bits);
            var storage = new ZeroBitStorage(LevelChunkSection.SECTION_SIZE);
            var newPalettedContainer = new PalettedContainer<BlockState>(registry, strat, config, storage,
                    List.of(uniqueStatesList[0]));
            sectionArray[sectionIndex] = new LevelChunkSection(newPalettedContainer, oldSection.getBiomes());
            return;
        }

        var useRegistry = bits > 8;
        if (useRegistry) {
            bits = Mth.ceillog2(registry.size());
        } else {
            var shiftBitsPow2 = Integer.max(ShiftMath.shiftForCeilPow2(bits), 2);
            bits = 1 << shiftBitsPow2;
        }

        var config = strat.<BlockState>getConfiguration(registry, bits);
        var valuesPerLong = Math.floorDiv(64, bits);
        var longs = new long[Math.ceilDiv(LevelChunkSection.SECTION_SIZE, valuesPerLong)];
        var storage = new SimpleBitStorage(bits, LevelChunkSection.SECTION_SIZE, longs);

        if (Integer.bitCount(bits) == 1) {

            for (int i = 0; i < longs.length; i++) {
                var value = 0L;
                for (int j = valuesPerLong - 1; j >= 0; j--) {
                    value <<= bits;
                    var idx = i * valuesPerLong + j;
                    var state = blocks[idx];
                    if (state == null) {
                        state = air;
                    }
                    if (useRegistry) {
                        value |= registry.getId(state);
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
        } else {

            for (int i = 0; i < LevelChunkSection.SECTION_SIZE; i++) {
                var state = blocks[i];
                if (state == null) {
                    state = air;
                }
                var value = registry.getId(state);
                storage.set(i, value);
            }
        }

        List<BlockState> stateList = null;
        if (!useRegistry) {
            stateList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                stateList.add(uniqueStatesList[i]);
            }
        }

        var newPalettedContainer = new PalettedContainer<BlockState>(registry, strat, config, storage, stateList);
        sectionArray[sectionIndex] = new LevelChunkSection(newPalettedContainer, oldSection.getBiomes());
    }
}
