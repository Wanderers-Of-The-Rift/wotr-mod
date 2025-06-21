package com.wanderersoftherift.wotr.world.level.levelgen.space;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.template.RiftGeneratable;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;

public record RoomRiftSpace(Vec3i size, Vec3i center, List<RiftSpaceCorridor> corridors, RiftGeneratable template,
        TripleMirror templateTransform) implements RiftSpace {

    @Override
    public Vec3i origin() {
        return center.offset(-size.getX() / 2, -size.getY() / 2, -size.getZ() / 2);
    }

    // old method for creating rooms, doesn't initialize RoomRiftSpace.template so don't use it
    @Deprecated
    public static RoomRiftSpace basicRiftSpace(Vec3i center, int levels, int tunnelLevel) {
        return new RoomRiftSpace(new Vec3i(3, levels, 3), center.offset(1, levels / 2, 1), List.of(
                new RiftSpaceCorridor(new Vec3i(1, tunnelLevel, 0), Direction.NORTH),
                new RiftSpaceCorridor(new Vec3i(1, tunnelLevel, 2), Direction.SOUTH),
                new RiftSpaceCorridor(new Vec3i(2, tunnelLevel, 1), Direction.EAST),
                new RiftSpaceCorridor(new Vec3i(0, tunnelLevel, 1), Direction.WEST)
        ), null, TripleMirror.NONE);
    }

    // old method for creating rooms, doesn't initialize RoomRiftSpace.template so don't use it
    @Deprecated
    public static RoomRiftSpace chaoticRiftSpace(Vec3i origin, Vec3i size) {
        var corridors = new ArrayList<RiftSpaceCorridor>(2 * size.getY() * (size.getX() + size.getZ()));
        for (int y = 0; y < size.getY(); y++) {
            for (int z = 0; z < size.getZ(); z++) {
                corridors.add(new RiftSpaceCorridor(new Vec3i(0, y, z), Direction.WEST));
                corridors.add(new RiftSpaceCorridor(new Vec3i(size.getX() - 1, y, z), Direction.EAST));
            }
            for (int x = 0; x < size.getX(); x++) {
                corridors.add(new RiftSpaceCorridor(new Vec3i(x, y, 0), Direction.NORTH));
                corridors.add(new RiftSpaceCorridor(new Vec3i(x, y, size.getZ() - 1), Direction.SOUTH));
            }
        }
        return new RoomRiftSpace(size, origin.offset(size.getX() / 2, size.getY() / 2, size.getZ() / 2), corridors,
                null, TripleMirror.NONE);
    }

    public RoomRiftSpace offset(int x, int y, int z) {
        return new RoomRiftSpace(size, center.offset(x, y, z), corridors, template, templateTransform);
    }
}
