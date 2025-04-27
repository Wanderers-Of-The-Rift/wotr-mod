package com.wanderersoftherift.wotr.util;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public record TripleMirror(boolean x, boolean z, boolean diagonal) {

    public TripleMirror(int permutation){
        this((permutation & 0b1) !=0,(permutation & 0b10) !=0,(permutation & 0b100) !=0);
    }

    public static final TripleMirror DIAGONAL = new TripleMirror(false,false,true);
    public static final TripleMirror NONE = new TripleMirror(false, false, false);

    public Vec3i applyToPosition(Vec3i position, int sizeX, int sizeZ){
        if(x) position = new Vec3i(sizeX-position.getX(), position.getY(), position.getZ());
        if(z) position = new Vec3i(position.getX(), position.getY(), sizeZ- position.getZ());
        if(diagonal) position = new Vec3i(position.getZ(), position.getY(), position.getX());

        return position;
    }

    public BlockState applyToBlockState(BlockState state){
        if(state==null)return null;
        if (x) state=state.mirror(Mirror.FRONT_BACK);
        if (z) state=state.mirror(Mirror.LEFT_RIGHT);
        if (diagonal) state=state.rotate(Rotation.CLOCKWISE_90).mirror(Mirror.FRONT_BACK);
        return state;
    }
}
