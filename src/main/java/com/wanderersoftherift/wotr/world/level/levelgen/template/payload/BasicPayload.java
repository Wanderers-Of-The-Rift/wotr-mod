package com.wanderersoftherift.wotr.world.level.levelgen.template.payload;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.util.FibonacciHashing;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PayloadRiftTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicPayload implements PayloadRiftTemplate.TemplatePayload {
    public static final int CHUNK_WIDTH = 16;
    private static final ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());

    private final BlockState[][] data;
    private final List<StructureTemplate.StructureEntityInfo> entities;
    private final Map<Vec3i, CompoundTag> tileEntities;
    private final CompoundTag[] fastTileEntityHashTable;
    private final Vec3i[] fastTileEntityPositionsHashTable;
    private final short[][] hidden;

    public BasicPayload(BlockState[][] data, List<StructureTemplate.StructureEntityInfo> entities,
            Map<Vec3i, CompoundTag> tileEntities, CompoundTag[] fastTileEntityHashTable,
            Vec3i[] fastTileEntityPositionsHashTable, short[][] hidden) {
        this.data = data;
        this.entities = entities;
        this.tileEntities = tileEntities;
        this.fastTileEntityHashTable = fastTileEntityHashTable;
        this.fastTileEntityPositionsHashTable = fastTileEntityPositionsHashTable;
        this.hidden = hidden;
    }

    public static BasicPayload of(
            StructureTemplate.Palette palette,
            Vec3i size,
            List<StructureTemplate.StructureEntityInfo> entities) {

        int chunkCount = size.getX() >> 4;
        if ((size.getX() & 0xf) > 0) {
            chunkCount++;
        }
        var blockStates = new BlockState[chunkCount][size.getY() * size.getZ() << 4];
        var blockEntities = new HashMap<Vec3i, CompoundTag>();

        List<StructureTemplate.StructureBlockInfo> blockInfos = palette.blocks();
        BlockState[] blockStateChunk = null;
        int lastChunkX = 0;
        for (int i = 0; i < blockInfos.size(); i++) {
            if (blockInfos.get(
                    i) instanceof StructureTemplate.StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt)) {
                var chunk = pos.getX() >> 4;
                if (blockStateChunk == null || chunk != lastChunkX) {
                    blockStateChunk = blockStates[chunk];
                    lastChunkX = chunk;
                }
                blockStateChunk[(pos.getX() & 0xf) + (pos.getZ() << 4) + (pos.getY() * size.getZ() << 4)] = state;
                if (nbt != null && !nbt.isEmpty()) {
                    blockEntities.put(pos, nbt);
                }
            }
        }

        Map<Vec3i, CompoundTag> tileEntities = new HashMap<>(blockEntities);
        var fastTileEntityHashTable = new CompoundTag[1024];
        var fastTileEntityPositionsHashTable = new Vec3i[1024];
        var iter = tileEntities.entrySet().iterator();
        while (iter.hasNext()) {
            var value = iter.next();
            var hash = hashTileEntityPosition(value.getKey().getX(), value.getKey().getY(), value.getKey().getZ());
            if (fastTileEntityHashTable[hash] == null) {
                fastTileEntityHashTable[hash] = value.getValue();
                fastTileEntityPositionsHashTable[hash] = value.getKey();
                iter.remove();
            }
        }
        if (tileEntities.isEmpty()) {
            tileEntities = null;
        } else if (tileEntities.size() == 1) {
            var entry = tileEntities.entrySet().stream().findFirst().get();
            tileEntities = Collections.singletonMap(entry.getKey(), entry.getValue());
        }
        return new BasicPayload(blockStates, entities, tileEntities, fastTileEntityHashTable,
                fastTileEntityPositionsHashTable, computeHidden(blockStates, size));
    }

    private static short[][] computeHidden(BlockState[][] data, Vec3i size) {
        var result = new short[data.length][data[0].length / 16];
        var offsetPos = new BlockPos.MutableBlockPos();
        for (int index1A = 0; index1A < data.length; index1A++) {
            var hiddenChunk = result[index1A];
            var dataChunk = data[index1A];

            for (int index1B = 0; index1B < dataChunk.length; index1B++) {
                var blockState = dataChunk[index1B];
                if (blockState != null) {
                    var blockPosX = (index1A << 4) + (index1B & 0xf);
                    var blockPosY = (index1B >> 4) / size.getZ();
                    var blockPosZ = (index1B >> 4) % size.getZ();
                    var isInvisible = blockState.canOcclude();
                    for (int i = 0; i < DIRECTIONS.size(); i++) {
                        var direction = DIRECTIONS.get(i);
                        if (!isInvisible) {
                            break;
                        }
                        offsetPos.set(blockPosX, blockPosY, blockPosZ);
                        offsetPos.move(direction);
                        if (offsetPos.getX() < 0 || offsetPos.getX() >= size.getX() || offsetPos.getY() < 0
                                || offsetPos.getY() >= size.getY() || offsetPos.getZ() < 0
                                || offsetPos.getZ() >= size.getZ()) {
                            continue;
                        }
                        var index2A = offsetPos.getX() >> 4;
                        var index2B = (offsetPos.getX() & 0xf) + (offsetPos.getZ() << 4)
                                + (offsetPos.getY() * size.getZ() << 4);
                        var blockState2 = data[index2A][index2B];
                        isInvisible = blockState2 != null && blockState2.canOcclude();
                    }
                    if (isInvisible) {
                        hiddenChunk[(index1B >> 4)] |= (short) (1 << (index1B & 0xf));
                    }
                }
            }
        }
        return result;
    }

    private static int hashTileEntityPosition(int x, int y, int z) {
        return ((x + (y << 6) + (z << 12)) * FibonacciHashing.GOLDEN_RATIO_INT) >>> 22;
    }

    private static int hashRoomChunkPosition(int x, int y, int z) {
        return ((x + (y << 2) + (z << 4)) * FibonacciHashing.GOLDEN_RATIO_INT) >>> 27;
    }

    @Override
    public void processPayloadBlocks(
            PayloadRiftTemplate template,
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            BlockPos offset,
            TripleMirror mirror) {
        var size = template.size();
        var processors = template.getTemplateProcessors();

        var emptyNBT = new CompoundTag();
        var mutablePosition = new BlockPos.MutableBlockPos();
        var xLastChunkPosition = 0;
        var yLastChunkPosition = 0;
        var zLastChunkPosition = 0;
        RiftProcessedChunk roomChunk = null;
        short[][] hidden = this.hidden;
        RiftProcessedChunk[] roomChunkHashTableCache = new RiftProcessedChunk[32];
        for (int i = 0; i < data.length; i++) {
            var templateChunk = data[i];
            var hiddenChunk = hidden[i];

            for (int j = 0; j < templateChunk.length; j++) {
                var blockState = mirror.applyToBlockState(templateChunk[j]);
                var isVisible = ((hiddenChunk[j >> 4] >> (j & 0xf)) & 1) == 0;
                if (blockState != null) {
                    var blockPosX = (i << 4) + (j & 0xf);
                    var blockPosY = (j >> 4) / size.getZ();
                    var blockPosZ = (j >> 4) % size.getZ();

                    mutablePosition.set(blockPosX, blockPosY, blockPosZ);
                    mirror.applyToMutablePosition(mutablePosition, size.getX() - 1, size.getZ() - 1);
                    mutablePosition.move(offset);

                    var xChunkPosition = mutablePosition.getX() >> 4;
                    var yChunkPosition = mutablePosition.getY() >> 4;
                    var zChunkPosition = mutablePosition.getZ() >> 4;

                    if (xLastChunkPosition != xChunkPosition || yLastChunkPosition != yChunkPosition
                            || zLastChunkPosition != zChunkPosition || roomChunk == null) {
                        xLastChunkPosition = xChunkPosition;
                        yLastChunkPosition = yChunkPosition;
                        zLastChunkPosition = zChunkPosition;
                        var hash = hashRoomChunkPosition(xChunkPosition, yChunkPosition, zChunkPosition);
                        var hashTableCached = roomChunkHashTableCache[hash];
                        if (hashTableCached != null && hashTableCached.origin.getX() == xChunkPosition
                                && hashTableCached.origin.getY() == yChunkPosition
                                && hashTableCached.origin.getZ() == zChunkPosition) {
                            roomChunk = hashTableCached;
                        } else {
                            roomChunk = (roomChunkHashTableCache[hash] = destination.getOrCreateChunk(xChunkPosition,
                                    yChunkPosition, zChunkPosition));
                            if (roomChunk == null) {
                                destination.getOrCreateChunk(xChunkPosition, yChunkPosition, zChunkPosition);
                                continue;
                            }
                        }
                    }
                    var xWithinChunk = mutablePosition.getX() & 0xf;
                    var yWithinChunk = mutablePosition.getY() & 0xf;
                    var zWithinChunk = mutablePosition.getZ() & 0xf;
                    if (roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) << 4) | ((yWithinChunk) << 8)] != null) {
                        continue;
                    }

                    var tileEntityHash = hashTileEntityPosition(blockPosX, blockPosY, blockPosZ);
                    var nbt = fastTileEntityHashTable[tileEntityHash];
                    if (nbt != null) {
                        var position = fastTileEntityPositionsHashTable[tileEntityHash];
                        if (position.getX() != blockPosX || position.getY() != blockPosY
                                || position.getZ() != blockPosZ) {
                            if (tileEntities == null) {
                                nbt = null;
                            } else {
                                var blockPos = new Vec3i(blockPosX, blockPosY, blockPosZ);
                                nbt = tileEntities.get(blockPos);
                            }
                        }
                        if (nbt == null) {
                            nbt = emptyNBT;
                        } else {
                            nbt = nbt.copy();
                        }
                    } else {
                        nbt = emptyNBT;
                    }
                    blockState = ((RiftTemplateProcessor) JigsawReplacementProcessor.INSTANCE).processBlockState(
                            blockState, mutablePosition.getX(), mutablePosition.getY(), mutablePosition.getZ(), world,
                            offset, nbt, isVisible);
                    for (int k = 0; k < processors.size() && blockState != null; k++) {
                        blockState = processors.get(k)
                                .processBlockState(blockState, mutablePosition.getX(), mutablePosition.getY(),
                                        mutablePosition.getZ(), world, offset, nbt, isVisible);
                    }
                    if (blockState == null) {
                        continue;
                    }
                    roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) << 4) | ((yWithinChunk) << 8)] = blockState;

                    if (!emptyNBT.isEmpty()) {
                        nbt = nbt.copy();
                        var added = emptyNBT.getAllKeys().toArray();
                        for (var key : added) {
                            emptyNBT.remove((String) key);
                        }
                    }
                    if (!nbt.isEmpty() && blockState.hasBlockEntity()) {
                        roomChunk.blockNBT[(xWithinChunk) | ((zWithinChunk) << 4) | ((yWithinChunk) << 8)] = nbt;
                    }
                }
            }
        }

    }

    @Override
    public void processPayloadEntities(
            PayloadRiftTemplate template,
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            BlockPos offset,
            TripleMirror mirror) {
        var size = template.size();
        var processors = template.getEntityProcessor();

        for (StructureTemplate.StructureEntityInfo it : entities) {
            var newNbt = it.nbt.copy();
            var position = mirror.applyToPosition(it.pos, size.getX(), size.getZ())
                    .add(offset.getX(), offset.getY(), offset.getZ());
            var blockPosition = mirror.applyToPosition(it.blockPos, size.getX() - 1, size.getZ() - 1)
                    .offset(offset.getX(), offset.getY(), offset.getZ());
            var info = new StructureTemplate.StructureEntityInfo(position,
                    new BlockPos((int) position.x, (int) position.y, (int) position.z), newNbt);
            mirror.applyToEntity(newNbt);

            var original = new StructureTemplate.StructureEntityInfo(position, new BlockPos(blockPosition), newNbt);
            info = JigsawReplacementProcessor.INSTANCE.processEntity(world, offset, original, info, null, null);
            for (var processor : processors) {
                if (info == null) {
                    break;
                }
                info = processor.processEntity(world, offset, original, info, null, null);
            }
            if (info == null) {
                continue;
            }

            destination.addEntity(info);
        }
    }
}
