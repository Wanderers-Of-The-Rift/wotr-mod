package com.wanderersoftherift.wotr.world.level.levelgen.layout;

import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import org.joml.Vector2i;

@Deprecated
public class GridTunnelRiftLayout implements RiftLayout {
    private final Vector2i period;
    private final Vector2i split;

    public GridTunnelRiftLayout(Vector2i period, Vector2i split) {
        this.period = period;
        this.split = split;
    }

    @Override
    public RiftSpace getChunkSpace(Vec3i chunkPos, MinecraftServer server) {
        var gridX = Math.floorDiv(chunkPos.getX(), 3);
        var gridZ = Math.floorDiv(chunkPos.getZ(), 3);

        var isTunnelX = Math.floorMod(gridX, period.x) >= split.x;
        var isTunnelZ = Math.floorMod(gridZ, period.y) >= split.y;

        RiftSpace space;

        // space would have to be provided by RoomRandomizer for this to work properly
        if (isTunnelX && isTunnelZ) {
            space = null;
        } else if (isTunnelX) {
            space = RoomRiftSpace.chaoticRiftSpace(new Vec3i(gridX * 3, 1, gridZ * 3), new Vec3i(1, 1, 3));

        } else if (isTunnelZ) {
            space = RoomRiftSpace.chaoticRiftSpace(new Vec3i(gridX * 3, 1, gridZ * 3), new Vec3i(3, 1, 1));
        } else {
            space = RoomRiftSpace.basicRiftSpace(new Vec3i(gridX * 3, 1, gridZ * 3), 3, 1
            );
        }
        if (chunkPos.getY() >= -1 && chunkPos.getY() <= 1) {
            return space;
        } else {
            return null;
        }
    }

    @Override
    public boolean validateCorridor(int x, int y, int z, Direction d, MinecraftServer server) {
        return true;
    }
}
