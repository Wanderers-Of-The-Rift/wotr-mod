package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.*;

//todo load and spawn entities
public class BasicRiftTemplate implements RiftGeneratable {
    public static final int CHUNK_WIDTH = 16;

    private final BlockState[][] data;
    private final Vec3i size;
    private final StructurePlaceSettings settings;
    private final HashMap<Vec3i, CompoundTag> tileEntities;
    private final List<StructureTemplate.JigsawBlockInfo> jigsaws;
    private final String identifier;



    public BasicRiftTemplate(BlockState[][] data, Vec3i size, StructurePlaceSettings settings, HashMap<Vec3i, CompoundTag> tileEntities, List<StructureTemplate.JigsawBlockInfo> jigsaws, String identifier, List<StructureTemplate.StructureEntityInfo> entities) {
        this.data = data;
        this.size = size;
        this.settings = settings;
        this.entities = entities;
        this.jigsaws = jigsaws;
    }


    @Override
    public Vec3i size() {
        return size;
    }

    @Override
    public Collection<StructureTemplate.JigsawBlockInfo> jigsaws() {
        return jigsaws;
    }

    @Override
    public void processAndPlace(RiftProcessedRoom destination, ServerLevel world, Vec3i placementShift, TripleMirror mirror){
        var threads = new ArrayList<Thread>();
        var offset = new BlockPos(destination.space.origin().multiply(16)).offset(placementShift);
        var mutablePosition = new BlockPos.MutableBlockPos();
        var xLastChunkPosition = 0;
        var yLastChunkPosition = 0;
        var zLastChunkPosition = 0;
        RiftProcessedChunk roomChunk=null;
        for (int i = 0; i < data.length; i++) {
            var _i = i;
            //threads.add(Thread.startVirtualThread(()->{
                var templateChunk = data[_i];

                for (int j = 0; j < templateChunk.length; j++) {
                    var blockState = mirror.applyToBlockState(templateChunk[j]);
                    if(blockState!=null) {
                        var blockPos = new Vec3i(_i*CHUNK_WIDTH+j % CHUNK_WIDTH, (j / CHUNK_WIDTH) / size.getZ(), (j / CHUNK_WIDTH) % size.getZ());

                        mutablePosition.set(offset).move(mirror.applyToPosition(blockPos, size.getX()-1, size.getZ()-1));

                        var xChunkPosition = mutablePosition.getX() >> 4;
                        var yChunkPosition = mutablePosition.getY() >> 4;
                        var zChunkPosition = mutablePosition.getZ() >> 4;

                        if (xLastChunkPosition!=xChunkPosition || yLastChunkPosition!=yChunkPosition || zLastChunkPosition!=zChunkPosition || roomChunk==null){
                            xLastChunkPosition = xChunkPosition;
                            yLastChunkPosition = yChunkPosition;
                            zLastChunkPosition = zChunkPosition;
                            roomChunk = destination.getOrCreateChunk(new Vec3i(xChunkPosition,yChunkPosition,zChunkPosition));
                        }
                        var xWithinChunk = mutablePosition.getX() & 0xf;
                        var yWithinChunk = mutablePosition.getY() & 0xf;
                        var zWithinChunk = mutablePosition.getZ() & 0xf;
                        if(roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) <<4) | ((yWithinChunk) <<8)]!=null)continue;
                        var nbt = tileEntities.get(blockPos);
                        nbt=nbt==null?new CompoundTag():nbt.copy();
                        var info = new StructureTemplate.StructureBlockInfo(mutablePosition, blockState, nbt);
                        if (settings!=null) {
                            var relative = StructureTemplate.calculateRelativePosition(settings, mutablePosition);
                            var original = new StructureTemplate.StructureBlockInfo(mutablePosition, blockState, nbt);
                            info=JigsawReplacementProcessor.INSTANCE.process(world,offset,offset,original,info,settings,null);
                            for (var processor : settings.getProcessors()) {

                                info = processor.process(world, offset/*pieceOrigin*/, offset/*structureOrigin*/, original, info, settings, null);
                            }
                            if (info == null) continue;
                        }
                        var finalPos = info.pos();
                        xChunkPosition = finalPos.getX() >> 4;
                        yChunkPosition = finalPos.getY() >> 4;
                        zChunkPosition = finalPos.getZ() >> 4;

                        if (xLastChunkPosition!=xChunkPosition || yLastChunkPosition!=yChunkPosition || zLastChunkPosition!=zChunkPosition || roomChunk==null){
                            xLastChunkPosition = xChunkPosition;
                            yLastChunkPosition = yChunkPosition;
                            zLastChunkPosition = zChunkPosition;
                            roomChunk = destination.getOrCreateChunk(new Vec3i(xChunkPosition,yChunkPosition,zChunkPosition));
                        }
                        xWithinChunk = finalPos.getX() & 0xf;
                        yWithinChunk = finalPos.getY() & 0xf;
                        zWithinChunk = finalPos.getZ() & 0xf;
                        roomChunk.blocks[(xWithinChunk) | ((zWithinChunk) <<4) | ((yWithinChunk) <<8)]=info.state();
                        nbt=info.nbt();
                        if(nbt!=null && !nbt.isEmpty())roomChunk.blockNBT[(xWithinChunk) | ((zWithinChunk) <<4) | ((yWithinChunk) <<8)] = nbt;
                    }
                }
           // }));
        }
        //todo some alternative for StructureTemplate.finalizeProcessing

        for (StructureTemplate.StructureEntityInfo it : entities) {
            var newNbt = it.nbt.copy();
            var position = mirror.applyToPosition(it.pos, size.getX(), size.getZ()).add(offset.getX(), offset.getY(), offset.getZ());
            var blockPosition = mirror.applyToPosition(it.blockPos, size.getX()-1, size.getZ()-1).offset(offset.getX(), offset.getY(), offset.getZ());
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
