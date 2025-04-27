package com.wanderersoftherift.wotr.world.level.levelgen.space;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class RoomRiftSpace implements RiftSpace {

    private final Vec3i size;
    private final Vec3i center;
    private final List<RiftSpaceCorridor> corridors;
    private final RoomType type;


    public RoomRiftSpace(Vec3i size, Vec3i center, List<RiftSpaceCorridor> corridors, RoomType type) {
        this.size = size;
        this.center = center;
        this.corridors = corridors;
        this.type = type;
    }

    @Override
    public Vec3i origin() {
        return center.offset(-size.getX()/2,-size.getY()/2,-size.getZ()/2);
    }

    @Override
    public Vec3i size() {
        return size;
    }

    @Override
    public List<RiftSpaceCorridor> corridors() {
        return corridors;
    }

    public static RoomRiftSpace basicRiftSpace(Vec3i center, int levels, int tunnelLevel, RoomType type){
        return new RoomRiftSpace(new Vec3i(3,levels,3), center, List.of(
                new RiftSpaceCorridor(new Vec3i(1,tunnelLevel,0), Direction.NORTH),
                new RiftSpaceCorridor(new Vec3i(1,tunnelLevel,2), Direction.SOUTH),
                new RiftSpaceCorridor(new Vec3i(2,tunnelLevel,1), Direction.EAST),
                new RiftSpaceCorridor(new Vec3i(0,tunnelLevel,1), Direction.WEST)
                ), type);
    }

    public static RoomRiftSpace chaoticRiftSpace(Vec3i center,Vec3i size){
        var corridors = new ArrayList<RiftSpaceCorridor>(2*size.getY()*(size.getX()+size.getZ()));
        for (int y = 0; y < size.getY(); y++) {
            for (int z = 0; z < size.getZ(); z++) {
                corridors.add(new RiftSpaceCorridor(new Vec3i(0,y,z),Direction.WEST));
                corridors.add(new RiftSpaceCorridor(new Vec3i(size.getX()-1,y,z),Direction.EAST));
            }
            for (int x = 0; x < size.getX(); x++) {
                corridors.add(new RiftSpaceCorridor(new Vec3i(x,y,0),Direction.NORTH));
                corridors.add(new RiftSpaceCorridor(new Vec3i(x,y, size.getZ()-1),Direction.SOUTH));
            }
        }
        return new RoomRiftSpace(size, center, corridors, RoomType.CHAOS);
    }
    public RoomRiftSpace offset(int x, int y, int z){
        return new RoomRiftSpace(size, center.offset(x,y,z), corridors,type);
    }

    public RoomType type() {
        return type;
    }

    public static enum RoomType{
        CHAOS, UNSTABLE, STABLE, PORTAL
    }
}
