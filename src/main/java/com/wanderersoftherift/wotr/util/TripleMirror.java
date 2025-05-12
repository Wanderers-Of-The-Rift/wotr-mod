package com.wanderersoftherift.wotr.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

public record TripleMirror(boolean x, boolean z, boolean diagonal) {

    public static final TripleMirror DIAGONAL = new TripleMirror(false,false,true);
    public static final TripleMirror NONE = new TripleMirror(false, false, false);

    public TripleMirror(int permutation){
        this((permutation & 0b1) !=0,(permutation & 0b10) !=0,(permutation & 0b100) !=0);
    }

    public Vec3i applyToPosition(Vec3i position, int sizeX, int sizeZ){
        if(x) {
            position = new Vec3i(sizeX - position.getX(), position.getY(), position.getZ());
        }
        if(z) {
            position = new Vec3i(position.getX(), position.getY(), sizeZ - position.getZ());
        }
        if(diagonal) {
            position = new Vec3i(position.getZ(), position.getY(), position.getX());
        }

        return position;
    }

    public Vec3 applyToPosition(Vec3 position, int sizeX, int sizeZ){
        if(x) {
            position = new Vec3(sizeX - position.x, position.y, position.z);
        }
        if(z) {
            position = new Vec3(position.x, position.y, sizeZ - position.z);
        }
        if(diagonal) {
            position = new Vec3(position.z, position.y, position.x);
        }

        return position;
    }

    public void applyToMutablePosition(BlockPos.MutableBlockPos position, int sizeX, int sizeZ){
        if(x) {
            position.set(sizeX - position.getX(), position.getY(), position.getZ());
        }
        if(z) {
            position.set(position.getX(), position.getY(), sizeZ - position.getZ());
        }
        if(diagonal) {
            position.set(position.getZ(), position.getY(), position.getX());
        }
    }

    public BlockState applyToBlockState(BlockState state){
        if(state==null) {
            return null;
        }
        if(state.getValues().isEmpty()) {
            return state;
        }
        if (x) {
            state = state.mirror(Mirror.FRONT_BACK);
        }
        if (z) {
            state = state.mirror(Mirror.LEFT_RIGHT);
        }
        if (diagonal) {
            state = state.rotate(Rotation.CLOCKWISE_90).mirror(Mirror.FRONT_BACK);
        }
        return state;
    }

    public Direction applyToDirection(Direction direction) {
        if((x && direction.getAxis()== Direction.Axis.X) || (z && direction.getAxis()== Direction.Axis.Z)) {
            direction = direction.getOpposite();
        }
        if(diagonal) {
            direction=switch (direction){
                case DOWN -> Direction.DOWN;
                case UP -> Direction.UP;
                case NORTH -> Direction.WEST;
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.NORTH;
                case EAST -> Direction.SOUTH;
            };
        }
        return direction;
    }

    public float applyToDegrees(float degrees){
        if (x) {
            degrees = -degrees;
        }
        if (z) {
            degrees = 180-degrees;
        }
        if (diagonal) {
            degrees = -90-degrees;
        }
        while (degrees>180){
            degrees-=360;
        }
        while (degrees<-180){
            degrees+=360;
        }
        return degrees;
    }

    public void applyToEntity(CompoundTag newNbt) {
        var rotationList = newNbt.getList("Rotation", CompoundTag.TAG_FLOAT);
        rotationList.set(0,FloatTag.valueOf(applyToDegrees(((FloatTag)rotationList.get(0)).getAsFloat())));
        if(newNbt.contains("Facing",CompoundTag.TAG_BYTE)){
            newNbt.putByte("Facing",(byte) applyToDirection(Direction.values()[newNbt.getInt("Facing")]).ordinal());
        }
        newNbt.put("Rotation", rotationList);
    }
}
