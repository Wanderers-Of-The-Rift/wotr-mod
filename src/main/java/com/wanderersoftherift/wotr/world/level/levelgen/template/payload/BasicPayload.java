package com.wanderersoftherift.wotr.world.level.levelgen.template.payload;

import com.wanderersoftherift.wotr.util.EnumEntries;
import com.wanderersoftherift.wotr.util.FibonacciHashing;
import com.wanderersoftherift.wotr.util.ShiftMath;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PayloadRiftTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicPayload implements PayloadRiftTemplate.TemplatePayload {
    public static final int PAYLOAD_CHUNK_WIDTH = 16;
    public static final int PAYLOAD_CHUNK_WIDTH_SHIFT = ShiftMath.shiftForCeilPow2(PAYLOAD_CHUNK_WIDTH);
    public static final int PAYLOAD_CHUNK_WIDTH_MASK = (1 << PAYLOAD_CHUNK_WIDTH_SHIFT) - 1;
    private static final CompoundTag EMPTY_TAG = new CompoundTag();

    private final BlockState[][] data;
    private final List<StructureTemplate.StructureEntityInfo> entities;
    private final Map<Vec3i, CompoundTag> tileEntities;
    private final CompoundTag[] fastTileEntityHashTable;
    private final Vec3i[] fastTileEntityPositionsHashTable;
    private final short[][] hidden;
    private final Vec3i size;

    public BasicPayload(BlockState[][] data, List<StructureTemplate.StructureEntityInfo> entities,
            Map<Vec3i, CompoundTag> tileEntities, CompoundTag[] fastTileEntityHashTable,
            Vec3i[] fastTileEntityPositionsHashTable, short[][] hidden, Vec3i size) {
        this.data = data;
        this.entities = entities;
        this.tileEntities = tileEntities;
        this.fastTileEntityHashTable = fastTileEntityHashTable;
        this.fastTileEntityPositionsHashTable = fastTileEntityPositionsHashTable;
        this.hidden = hidden;
        this.size = size;
    }

    public static BasicPayload of(
            StructureTemplate.Palette palette,
            Vec3i size,
            List<StructureTemplate.StructureEntityInfo> entities) {

        int chunkCount = size.getX() >> PAYLOAD_CHUNK_WIDTH_SHIFT;
        if ((size.getX() & PAYLOAD_CHUNK_WIDTH_MASK) > 0) {
            chunkCount++;
        }
        var blockStates = new BlockState[chunkCount][size.getY() * size.getZ() << PAYLOAD_CHUNK_WIDTH_SHIFT];
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
                blockStateChunk[(pos.getX() & PAYLOAD_CHUNK_WIDTH_MASK) + (pos.getZ() << PAYLOAD_CHUNK_WIDTH_SHIFT)
                        + (pos.getY() * size.getZ() << PAYLOAD_CHUNK_WIDTH_SHIFT)] = state;
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
                fastTileEntityPositionsHashTable, computeHidden(blockStates, size), size);
    }

    private static short[][] computeHidden(BlockState[][] data, Vec3i size) {
        var result = new short[data.length][data[0].length >> PAYLOAD_CHUNK_WIDTH_SHIFT];
        var offsetPos = new BlockPos.MutableBlockPos();
        for (int index1A = 0; index1A < data.length; index1A++) {
            var hiddenChunk = result[index1A];
            var dataChunk = data[index1A];

            for (int index1B = 0; index1B < dataChunk.length; index1B++) {
                var blockState = dataChunk[index1B];
                if (blockState == null) {
                    continue;
                }
                var blockPosX = (index1A << PAYLOAD_CHUNK_WIDTH_SHIFT) + (index1B & PAYLOAD_CHUNK_WIDTH_MASK);
                var blockPosY = (index1B >> PAYLOAD_CHUNK_WIDTH_SHIFT) / size.getZ();
                var blockPosZ = (index1B >> PAYLOAD_CHUNK_WIDTH_SHIFT) % size.getZ();
                var isInvisible = blockState.canOcclude();
                for (int i = 0; i < EnumEntries.DIRECTIONS.size(); i++) {
                    var direction = EnumEntries.DIRECTIONS.get(i);
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
                    var index2A = offsetPos.getX() >> PAYLOAD_CHUNK_WIDTH_SHIFT;
                    var index2B = (offsetPos.getX() & PAYLOAD_CHUNK_WIDTH_MASK)
                            + (offsetPos.getZ() << PAYLOAD_CHUNK_WIDTH_SHIFT)
                            + (offsetPos.getY() * size.getZ() << PAYLOAD_CHUNK_WIDTH_SHIFT);
                    var blockState2 = data[index2A][index2B];
                    isInvisible = blockState2 != null && blockState2.canOcclude();
                }
                if (isInvisible) {
                    hiddenChunk[(index1B >> PAYLOAD_CHUNK_WIDTH_SHIFT)] |= (short) (1 << (index1B
                            & PAYLOAD_CHUNK_WIDTH_MASK));
                }
            }
        }
        return result;
    }

    private static int hashTileEntityPosition(int x, int y, int z) {
        return ((x + (y << 6) + (z << 12)) * FibonacciHashing.GOLDEN_RATIO_INT) >>> 22;
    }

    @Override
    public void processPayloadBlocks(
            PayloadRiftTemplate template,
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            BlockPos offset,
            TripleMirror mirror) {

        var mutablePosition = new BlockPos.MutableBlockPos();
        var xLastChunkPosition = 0;
        var yLastChunkPosition = 0;
        var zLastChunkPosition = 0;
        RiftProcessedChunk roomChunk = null;
        short[][] hidden = this.hidden;
        for (int i = 0; i < data.length; i++) {
            var templateChunk = data[i];
            var hiddenChunk = hidden[i];

            for (int j = 0; j < templateChunk.length; j++) {
                var blockState = mirror.applyToBlockState(templateChunk[j]);
                var isVisible = ((hiddenChunk[j >> PAYLOAD_CHUNK_WIDTH_SHIFT] >> (j & PAYLOAD_CHUNK_WIDTH_MASK))
                        & 1) == 0;
                if (blockState != null) {
                    var blockPosX = (i << PAYLOAD_CHUNK_WIDTH_SHIFT) + (j & PAYLOAD_CHUNK_WIDTH_MASK);
                    var blockPosY = (j >> PAYLOAD_CHUNK_WIDTH_SHIFT) / size.getZ();
                    var blockPosZ = (j >> PAYLOAD_CHUNK_WIDTH_SHIFT) % size.getZ();

                    mutablePosition.set(blockPosX, blockPosY, blockPosZ);
                    mirror.applyToMutablePosition(mutablePosition, size.getX() - 1, size.getZ() - 1);
                    mutablePosition.move(offset);

                    var xChunkPosition = mutablePosition.getX() >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;
                    var yChunkPosition = mutablePosition.getY() >> RiftProcessedChunk.CHUNK_HEIGHT_SHIFT;
                    var zChunkPosition = mutablePosition.getZ() >> RiftProcessedChunk.CHUNK_WIDTH_SHIFT;

                    if (xLastChunkPosition != xChunkPosition || yLastChunkPosition != yChunkPosition
                            || zLastChunkPosition != zChunkPosition || roomChunk == null) {
                        xLastChunkPosition = xChunkPosition;
                        yLastChunkPosition = yChunkPosition;
                        zLastChunkPosition = zChunkPosition;
                        roomChunk = destination.getOrCreateChunk(xChunkPosition, yChunkPosition, zChunkPosition);
                        if (roomChunk == null) {
                            continue;
                        }
                    }
                    if (roomChunk.blocks[(mutablePosition.getX() & RiftProcessedChunk.CHUNK_WIDTH_MASK)
                            | ((mutablePosition.getZ()
                                    & RiftProcessedChunk.CHUNK_WIDTH_MASK) << RiftProcessedChunk.CHUNK_WIDTH_SHIFT)
                            | ((mutablePosition.getY()
                                    & RiftProcessedChunk.CHUNK_HEIGHT_MASK) << RiftProcessedChunk.CHUNK_WIDTH_SHIFT_2)] != null) {
                        continue;
                    }

                    var nbt = getTileEntity(blockPosX, blockPosY, blockPosZ);
                    BlockEntity entity = null;
                    if (!nbt.isEmpty() && nbt.get("id") instanceof StringTag) {
                        entity = BlockEntity.loadStatic(mutablePosition, blockState, nbt, world.registryAccess());
                    }
                    template.processBlock(blockState, mutablePosition, world, offset, entity, isVisible, roomChunk);
                }
            }
        }

    }

    private CompoundTag getTileEntity(int blockPosX, int blockPosY, int blockPosZ) {
        var tileEntityHash = hashTileEntityPosition(blockPosX, blockPosY, blockPosZ);
        var nbt = fastTileEntityHashTable[tileEntityHash];
        if (nbt == null) {
            return EMPTY_TAG;
        }
        var position = fastTileEntityPositionsHashTable[tileEntityHash];
        if (position.getX() != blockPosX || position.getY() != blockPosY || position.getZ() != blockPosZ) {
            if (tileEntities == null) {
                nbt = null;
            } else {
                var blockPos = new Vec3i(blockPosX, blockPosY, blockPosZ);
                nbt = tileEntities.get(blockPos);
            }
        }
        if (nbt == null) {
            return EMPTY_TAG;
        }
        return nbt;
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
