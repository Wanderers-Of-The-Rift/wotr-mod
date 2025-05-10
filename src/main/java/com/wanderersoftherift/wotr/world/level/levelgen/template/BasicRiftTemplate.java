package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.FibonacciHashing;
import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.RiftTemplateProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicRiftTemplate implements RiftGeneratable {
    public static final int CHUNK_WIDTH = 16;

    private final BlockState[][] data;
    private final Vec3i size;
    private final StructurePlaceSettings settings;
    private final List<StructureTemplate.StructureEntityInfo> entities;
    private final Map<Vec3i, CompoundTag> tileEntities;
    private final List<StructureTemplate.JigsawBlockInfo> jigsaws;
    private final String identifier;

    private final CompoundTag[] fastTileEntityHashTable;
    private final Vec3i[] fastTileEntityPositionsHashTable;

    private final short[][] hidden;

    public BasicRiftTemplate(BlockState[][] data, Vec3i size, StructurePlaceSettings settings, Map<Vec3i, CompoundTag> tileEntities, List<StructureTemplate.JigsawBlockInfo> jigsaws, String identifier, List<StructureTemplate.StructureEntityInfo> entities) {
        this.data = data;
        this.size = size;
        this.settings = settings;
        this.entities = entities;
        this.jigsaws = jigsaws;
        this.identifier = identifier;

        tileEntities = new HashMap<>(tileEntities);
        fastTileEntityHashTable = new CompoundTag[1024];
        fastTileEntityPositionsHashTable = new Vec3i[1024];
        var iter = tileEntities.entrySet().iterator();
        while (iter.hasNext()){
            var value = iter.next();
            var hash = hashTileEntityPosition(value.getKey().getX(), value.getKey().getY(), value.getKey().getZ());
            if(fastTileEntityHashTable[hash] == null){
                fastTileEntityHashTable[hash] = value.getValue();
                fastTileEntityPositionsHashTable[hash] = value.getKey();
                iter.remove();
            }
        }
        if(tileEntities.isEmpty()){
            tileEntities=null;
        }else if(tileEntities.size() == 1){
            var entry = tileEntities.entrySet().stream().findFirst().get();
            tileEntities=Collections.singletonMap(entry.getKey(), entry.getValue());
        }
        this.tileEntities=tileEntities;

        hidden = new short[data.length][data[0].length/16];
        for (int i = 0; i < data.length; i++) {
            var hiddenChunk = hidden[i];
            var dataChunk = data[i];

            for (int j = 0; j < dataChunk.length; j++) {
                var blockState = dataChunk[j];
                if (blockState != null) {
                    var blockPosX = i * CHUNK_WIDTH + j % CHUNK_WIDTH;
                    var blockPosY = (j / CHUNK_WIDTH) / size.getZ();
                    var blockPosZ = (j / CHUNK_WIDTH) % size.getZ();
                    var blockPos = new BlockPos(blockPosX, blockPosY, blockPosZ);
                    var isInvisible = blockState.canOcclude();
                    for (var direction: Direction.values()){
                        if (!isInvisible) break;
                        var offsetPos = blockPos.relative(direction);
                        if(offsetPos.getX() < 0 || offsetPos.getX() >= size.getX() ||
                                offsetPos.getY() < 0 || offsetPos.getY() >= size.getY() ||
                                offsetPos.getZ() < 0 || offsetPos.getZ() >= size.getZ()
                        ) continue;
                        var index2_0 = offsetPos.getX() / CHUNK_WIDTH;
                        var index2_1 = offsetPos.getX() % CHUNK_WIDTH + offsetPos.getZ() * CHUNK_WIDTH + offsetPos.getY() * CHUNK_WIDTH * size.getZ();
                        var blockState2 = data[index2_0][index2_1];
                        isInvisible = blockState2 != null && blockState2.canOcclude();
                    }
                    if (isInvisible) {
                        hiddenChunk[j / CHUNK_WIDTH] |= (short) (1 << (j % CHUNK_WIDTH));
                    }
                }
            }
        }
    }

    private static int hashTileEntityPosition(int x, int y, int z){
        return ((x + (y << 6) + (z << 12)) * FibonacciHashing.GOLDEN_RATIO_INT) >>> 22;
    }
    private static int hashRoomChunkPosition(int x, int y, int z){
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

    //what a mess...
    @Override
    public void processAndPlace(RiftProcessedRoom destination, ServerLevel world, Vec3i placementShift, TripleMirror mirror){
        var emptyNBT = new CompoundTag();
        var offset = new BlockPos(destination.space.origin().multiply(16)).offset(placementShift);
        var mutablePosition = new BlockPos.MutableBlockPos();
        var xLastChunkPosition = 0;
        var yLastChunkPosition = 0;
        var zLastChunkPosition = 0;
        RiftProcessedChunk roomChunk=null;
        RiftProcessedChunk[] roomChunkHashTableCache = new RiftProcessedChunk[32];
        for (int i = 0; i < data.length; i++) {
            var templateChunk = data[i];
            var hiddenChunk = hidden[i];

            for (int j = 0; j < templateChunk.length; j++) {
                var blockState = mirror.applyToBlockState(templateChunk[j]);
                var isVisible = ((hiddenChunk[j/CHUNK_WIDTH] >> (j%CHUNK_WIDTH)) & 1) == 0;
                if(blockState!=null) {
                    var blockPosX = i * CHUNK_WIDTH + j % CHUNK_WIDTH;
                    var blockPosY = (j / CHUNK_WIDTH) / size.getZ();
                    var blockPosZ = (j / CHUNK_WIDTH) % size.getZ();

                    mutablePosition.set(blockPosX,blockPosY, blockPosZ);
                    mirror.applyToMutablePosition(mutablePosition, size.getX() - 1, size.getZ() - 1);
                    mutablePosition.move(offset);

                    var xChunkPosition = mutablePosition.getX() >> 4;
                    var yChunkPosition = mutablePosition.getY() >> 4;
                    var zChunkPosition = mutablePosition.getZ() >> 4;

                    if (xLastChunkPosition != xChunkPosition || yLastChunkPosition != yChunkPosition || zLastChunkPosition != zChunkPosition || roomChunk == null){
                        xLastChunkPosition = xChunkPosition;
                        yLastChunkPosition = yChunkPosition;
                        zLastChunkPosition = zChunkPosition;
                        var hash = hashRoomChunkPosition(xChunkPosition,yChunkPosition,zChunkPosition);
                        var hashTableCached = roomChunkHashTableCache[hash];
                        if(hashTableCached != null && hashTableCached.origin.getX() == xChunkPosition && hashTableCached.origin.getY() == yChunkPosition && hashTableCached.origin.getZ() == zChunkPosition){
                            roomChunk = hashTableCached;
                        }else {
                            roomChunk = (roomChunkHashTableCache[hash] = destination.getOrCreateChunk(new Vec3i(xChunkPosition, yChunkPosition, zChunkPosition)));
                        }
                    }
                    var xWithinChunk = mutablePosition.getX() & 0xf;
                    var yWithinChunk = mutablePosition.getY() & 0xf;
                    var zWithinChunk = mutablePosition.getZ() & 0xf;
                    if(roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) <<4) | ((yWithinChunk) <<8)]!=null)continue;

                    var tileEntityHash = hashTileEntityPosition(blockPosX, blockPosY, blockPosZ);
                    var nbt = fastTileEntityHashTable[tileEntityHash];
                    if(nbt!=null){
                        var position = fastTileEntityPositionsHashTable[tileEntityHash];
                        if(position.getX()!=blockPosX || position.getY()!=blockPosY || position.getZ()!=blockPosZ){
                            if(tileEntities==null){
                                nbt=null;
                            }else {
                                var blockPos = new Vec3i(blockPosX, blockPosY, blockPosZ );
                                nbt = tileEntities.get(blockPos);
                            }
                        }
                        if(nbt==null){
                            nbt = emptyNBT;
                        } else {
                            nbt = nbt.copy();
                        }
                    } else {
                        nbt = emptyNBT;
                    }
                    if (settings != null) {
                        blockState = ((RiftTemplateProcessor)JigsawReplacementProcessor.INSTANCE).processBlockState(blockState, mutablePosition.getX(), mutablePosition.getY(), mutablePosition.getZ(), world, offset, nbt, isVisible);
                        List<StructureProcessor> processors = settings.getProcessors();
                        for (int k = 0; k < processors.size(); k++) {
                            var processor = processors.get(k);
                            if (blockState == null) break;
                            if (!(processor instanceof RiftTemplateProcessor)) {
                                //WanderersOfTheRift.LOGGER.debug("processor not rift processor: " + processor.getClass());
                            } else
                                blockState = ((RiftTemplateProcessor) processor).processBlockState(blockState, mutablePosition.getX(), mutablePosition.getY(), mutablePosition.getZ(), world, offset, nbt, isVisible);
                        }
                        if (blockState == null) continue;
                    }

                    if (xLastChunkPosition!=xChunkPosition || yLastChunkPosition != yChunkPosition || zLastChunkPosition != zChunkPosition || roomChunk == null){
                        xLastChunkPosition = xChunkPosition;
                        yLastChunkPosition = yChunkPosition;
                        zLastChunkPosition = zChunkPosition;

                        var hash = hashRoomChunkPosition(xChunkPosition,yChunkPosition,zChunkPosition);
                        var hashTableCached = roomChunkHashTableCache[hash];
                        if(hashTableCached != null && hashTableCached.origin.getX() == xChunkPosition && hashTableCached.origin.getY() == yChunkPosition && hashTableCached.origin.getZ() == zChunkPosition){
                            roomChunk = hashTableCached;
                        }else {
                            roomChunk = (roomChunkHashTableCache[hash] = destination.getOrCreateChunk(new Vec3i(xChunkPosition,yChunkPosition,zChunkPosition)));
                        }
                    };
                    roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) <<4 ) | ((yWithinChunk) << 8)] = blockState;

                    if(!emptyNBT.isEmpty()) {
                        if(nbt != null) {
                            nbt = nbt.copy();
                        }
                        var added = emptyNBT.getAllKeys().toArray();
                        for (var key:added)emptyNBT.remove((String) key);
                    }
                    if(nbt!=null && !nbt.isEmpty() && blockState.hasBlockEntity()) {
                        roomChunk.blockNBT[(xWithinChunk) | ((zWithinChunk) << 4) | ((yWithinChunk) << 8)] = nbt;
                    }
                }
            }
        }
        //todo some alternative for StructureTemplate.finalizeProcessing

        for (StructureTemplate.StructureEntityInfo it : entities) {
            var newNbt = it.nbt.copy();
            var position = mirror.applyToPosition(it.pos, size.getX(), size.getZ()).add(offset.getX(), offset.getY(), offset.getZ());
            var blockPosition = mirror.applyToPosition(it.blockPos, size.getX() - 1, size.getZ() - 1).offset(offset.getX(), offset.getY(), offset.getZ());
            var info = new StructureTemplate.StructureEntityInfo(position, new BlockPos((int) position.x, (int) position.y, (int) position.z), newNbt);
            mirror.applyToEntity(newNbt);

            if (settings != null) {
                var original = new StructureTemplate.StructureEntityInfo(position, new BlockPos(blockPosition), newNbt);
                info = JigsawReplacementProcessor.INSTANCE.processEntity(world, offset, original, info, settings, null);
                for (var processor : settings.getProcessors()) {
                    if (info == null) break;
                    info = processor.processEntity(world, offset, original, info, settings, null);
                }
                if (info == null) continue;
            }
            destination.addEntity(info);
        }
    }

    public String identifier() {
        return identifier;
    }
}
