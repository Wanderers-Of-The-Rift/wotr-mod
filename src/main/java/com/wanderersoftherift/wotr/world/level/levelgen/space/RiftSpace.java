package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;

/**
 * This describes volume allocated for room to generate in
 *
 * size units are in chunks
 */
public interface RiftSpace {


    public static void placeInChunk(ChunkAccess chunk, RiftSpace space, int level, BlockState perimeterBlock) {
        var processedChunk = new RiftProcessedChunk(new Vec3i(chunk.getPos().x, level, chunk.getPos().z),null);
        placeInRiftChunk(processedChunk, space, perimeterBlock);
        processedChunk.placeInWorld(chunk,null);

    }

    public static void placeInRiftChunk(RiftProcessedChunk chunk, RiftSpace space, BlockState perimeterBlock) {
        var level = chunk.origin.getY();
        if(space==null || space instanceof VoidRiftSpace){
            for (int y = level*16; y < level*16+16; y++) {
                for (int xz = 0; xz < 256; xz++) {
                    var z = (xz >>> 4)&0xf;
                    var x = xz&0xf;
                    chunk.setBlockState(x, y, z, Blocks.BEDROCK.defaultBlockState());
                    chunk.setBlockState(x, y, z, perimeterBlock);
                }
            }
        }else if(level>=space.origin().getY() && level<space.origin().getY()+space.size().getY()){

            var originRelativeX = chunk.origin.getX()-space.origin().getX();
            var originRelativeY = level-space.origin().getY();
            var originRelativeZ = chunk.origin.getZ()-space.origin().getZ();
            var corridors = space.corridors().stream().filter((corridor)->corridor.position().getX()+space.origin().getX()==chunk.origin.getX() && corridor.position().getZ()+space.origin().getZ()==chunk.origin.getZ() && corridor.position().getY()+space.origin().getY()==level).toList();
            var hasCorridorNorth = corridors.stream().anyMatch((it)->it.direction()== Direction.NORTH);
            var hasCorridorWest = corridors.stream().anyMatch((it)->it.direction()== Direction.WEST);
            for (int y = level*16; y < level*16+16; y++) {
                for (int x = 0; x < 16 && originRelativeZ<=0; x++) {
                    if(!hasCorridorNorth || x<=6 || x>=10 || y<=5+level*16 || y>=11+level*16) {
                        chunk.setBlockState(x, y, 0, Blocks.BEDROCK.defaultBlockState()); //todo make add blockstate parameter instead of hardcoded bedrock
                        chunk.setBlockState(x, y, 0, perimeterBlock);
                    }
                }
                for (int z = 0; z < 16 && originRelativeX<=0; z++) {
                    if(!hasCorridorWest || z<=6 || z>=10 || y<=5+level*16 || y>=11+level*16) {
                        chunk.setBlockState(0, y, z, Blocks.BEDROCK.defaultBlockState());
                        chunk.setBlockState(0, y, z, perimeterBlock);
                    }
                }
                for (int xz = 0; xz < 256 && originRelativeY<=0 && y<=level*16; xz++) {
                    var z = (xz >>> 4)&0xf;
                    var x = xz&0xf;
                    chunk.setBlockState(x,y,z, Blocks.BEDROCK.defaultBlockState());
                    chunk.setBlockState(x, y, z, perimeterBlock);
                }

            }
        }
    }

    Vec3i origin();
    Vec3i size();
    List<RiftSpaceCorridor> corridors();

    //todo add information about template used in the room
}
