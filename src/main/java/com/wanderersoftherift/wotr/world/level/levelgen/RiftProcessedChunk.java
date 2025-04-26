package com.wanderersoftherift.wotr.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class RiftProcessedChunk {

    public final Vec3i origin;
    public final BlockState[] blocks = new BlockState[4096];
    public final CompoundTag[] blockNBT = new CompoundTag[4096];
    public final RiftProcessedRoom parentRoom;

    public RiftProcessedChunk(Vec3i origin, RiftProcessedRoom parentRoom) {
        this.origin = origin;
        this.parentRoom = parentRoom;
    }

    public void placeInWorld(ChunkAccess chunk, LevelAccessor level){
        var mutablePosition = new BlockPos.MutableBlockPos();
        for (int index = 0; index < 4096; index++) {
            var block = blocks[index];
            if(block==null)continue;
            var x = index & 0xf;
            var z = (index >> 4) & 0xf;
            var y = (index >> 8) & 0xf;
            mutablePosition.set(x,y+16*origin.getY(),z);
            if(chunk.getBlockState(mutablePosition).is(Blocks.BEDROCK))continue;
            chunk.setBlockState(mutablePosition,block,false);
            var nbt = blockNBT[index];
            if(nbt!=null){
                nbt.putInt("x",x | (origin.getX()<<4));
                nbt.putInt("y",y | (origin.getY()<<4));
                nbt.putInt("z",z | (origin.getZ()<<4));
                chunk.setBlockEntityNbt(nbt);
                level.getBlockEntity(mutablePosition.move((origin.getX()<<4),0,(origin.getZ()<<4)));
            }
        }
    }


    public void setBlockState(BlockPos.MutableBlockPos position, BlockState blockState) {
        setBlockState(position.getX(), position.getY(), position.getZ(), blockState);
    }

    public void setBlockState(int x, int y, int z, BlockState blockState) {
        var index = x + z*16 + (y-origin.getY()*16)*256;
        blocks[index]=blockState;
    }


    public BlockState getBlockState(BlockPos.MutableBlockPos position) {
        return getBlockState(position.getX(), position.getY(), position.getZ());
    }

    public BlockState getBlockState(int x, int y, int z) {
        var index = x + z*16 + (y-origin.getY()*16)*256;
        return blocks[index];
    }


    /**
    * this should at some point replace placeInWorld
    * */
    public LevelChunkSection convertToMinecraftSection(){
        //todo
        return new LevelChunkSection(null,null);
    }
}
