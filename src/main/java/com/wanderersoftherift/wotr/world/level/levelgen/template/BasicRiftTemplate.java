package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.util.FibonacciHashing;
import com.wanderersoftherift.wotr.util.Ref;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftFinalProcessor;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.JigsawReplacementProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.NopProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// superseded by PayloadRiftTemplate
@Deprecated
public class BasicRiftTemplate implements RiftGeneratable {
    public static final int CHUNK_WIDTH = 16;
    private static final ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());

    private final BlockState[][] data;
    private final Vec3i size;
    private final List<StructureProcessor> entityProcessor;
    private final List<RiftFinalProcessor> finalProcessors;
    private final List<RiftTemplateProcessor> templateProcessors;
    private final List<StructureTemplate.StructureEntityInfo> entities;
    private final Map<Vec3i, CompoundTag> tileEntities;
    private final List<StructureTemplate.JigsawBlockInfo> jigsaws;
    private final String identifier;

    private final CompoundTag[] fastTileEntityHashTable;
    private final Vec3i[] fastTileEntityPositionsHashTable;

    private final Future<short[][]> hidden;

    public BasicRiftTemplate(BlockState[][] data, Vec3i size, StructurePlaceSettings settings,
            Map<Vec3i, CompoundTag> tileEntities, List<StructureTemplate.JigsawBlockInfo> jigsaws, String identifier,
            List<StructureTemplate.StructureEntityInfo> entities) {
        this.data = data;
        this.size = size;
        var templateProcessors = new ArrayList<RiftTemplateProcessor>();
        var finalProcessors = new ArrayList<RiftFinalProcessor>();

        for (StructureProcessor processor : settings.getProcessors()) {
            var used = false;
            if (processor instanceof RiftTemplateProcessor riftTemplateProcessor) {
                templateProcessors.add(riftTemplateProcessor);
                used = true;
            }
            if (processor instanceof RiftFinalProcessor riftFinalProcessor) {
                finalProcessors.add(riftFinalProcessor);
                used = true;
            }
            if (!used && !(processor instanceof NopProcessor)) {
                WanderersOfTheRift.LOGGER.warn("incompatible processor type: {}", processor.getClass());
            }
        }

        this.templateProcessors = ImmutableList.copyOf(templateProcessors);
        this.finalProcessors = ImmutableList.copyOf(finalProcessors);
        this.entityProcessor = ImmutableList.copyOf(settings.getProcessors());

        this.entities = entities;
        this.jigsaws = jigsaws;
        this.identifier = identifier;

        tileEntities = new HashMap<>(tileEntities);
        fastTileEntityHashTable = new CompoundTag[1024];
        fastTileEntityPositionsHashTable = new Vec3i[1024];
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
        this.tileEntities = tileEntities;

        hidden = CompletableFuture.supplyAsync(() -> computeHidden(data, size), Thread::startVirtualThread);
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
    public Vec3i size() {
        return size;
    }

    @Override
    public Collection<StructureTemplate.JigsawBlockInfo> jigsaws() {
        return jigsaws;
    }

    // what a mess...
    @Override
    public void processAndPlace(
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            Vec3i placementShift,
            TripleMirror mirror) {
        var emptyNBT = new CompoundTag();
        var offset = new BlockPos(destination.space.origin().multiply(16)).offset(placementShift);
        var mutablePosition = new BlockPos.MutableBlockPos();
        var xLastChunkPosition = 0;
        var yLastChunkPosition = 0;
        var zLastChunkPosition = 0;
        RiftProcessedChunk roomChunk = null;
        short[][] hidden = null;
        try {
            hidden = this.hidden.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
                        }
                    } else {
                        nbt = emptyNBT;
                    }
                    BlockEntity entity = null;
                    if (nbt != null && !nbt.isEmpty() && nbt.get("id") instanceof StringTag) {
                        entity = BlockEntity.loadStatic(mutablePosition, blockState, nbt, world.registryAccess());
                    }
                    var entityRef = new Ref<BlockEntity>(entity);
                    blockState = ((RiftTemplateProcessor) JigsawReplacementProcessor.INSTANCE).processBlockState(
                            blockState, mutablePosition.getX(), mutablePosition.getY(), mutablePosition.getZ(), world,
                            offset, entityRef, isVisible);
                    var processors = templateProcessors;
                    for (int k = 0; k < processors.size() && blockState != null; k++) {
                        blockState = processors.get(k)
                                .processBlockState(blockState, mutablePosition.getX(), mutablePosition.getY(),
                                        mutablePosition.getZ(), world, offset, entityRef, isVisible);
                    }
                    if (blockState == null) {
                        continue;
                    }
                    roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) << 4) | ((yWithinChunk) << 8)] = blockState;

                    entity = entityRef.getValue();
                    if (entity != null && blockState.hasBlockEntity()) {
                        roomChunk.blockEntities.add(entity);
                    }
                }
            }
        }

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
            for (var processor : entityProcessor) {
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

        var processors = finalProcessors;
        for (int k = 0; k < processors.size(); k++) {
            processors.get(k).finalizeRoomProcessing(destination, world, offset, size);
        }
    }

    public String identifier() {
        return identifier;
    }
}
