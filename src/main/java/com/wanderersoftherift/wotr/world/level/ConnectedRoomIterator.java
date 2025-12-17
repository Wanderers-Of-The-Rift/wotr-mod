package com.wanderersoftherift.wotr.world.level;

import com.wanderersoftherift.wotr.entity.portal.PortalSpawnLocation;
import com.wanderersoftherift.wotr.world.level.levelgen.layout.RiftLayout;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RiftSpace;
import com.wanderersoftherift.wotr.world.level.levelgen.space.RoomRiftSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Iterator over rift rooms connected to an origin room
 */
public class ConnectedRoomIterator implements Iterator<RoomRiftSpace> {

    private static final int DEFAULT_MAX_DEPTH = 10;

    private final int maxDepth;
    private final RiftLayout layout;

    private final Set<Vec3i> visitedRooms = new HashSet<>();
    private Deque<RoomRiftSpace> currentLayer = new ArrayDeque<>();
    private Deque<RoomRiftSpace> nextLayer = new ArrayDeque<>();
    private int depth = 0;

    private ConnectedRoomIterator(RiftLayout layout, SectionPos start, int maxDepth) {
        this.layout = layout;
        this.maxDepth = maxDepth;
        if ((layout.getChunkSpace(start) instanceof RoomRiftSpace startRoom)) {
            visitedRooms.add(startRoom.origin());
            currentLayer.add(startRoom);
        }
    }

    /**
     * @param level The level to iterate rooms in
     * @return An iterator over the rooms in the level, from the portal room up to 10 rooms deep
     */
    public static Iterator<RoomRiftSpace> create(ServerLevel level) {
        return create(level, PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION, DEFAULT_MAX_DEPTH);
    }

    /**
     * @param level    The level to iterate rooms in
     * @param maxDepth The maximum numbers of rooms deep to iterate
     * @return An iterator over the rooms in the level, from the portal room
     */
    public static Iterator<RoomRiftSpace> create(ServerLevel level, int maxDepth) {
        return create(level, PortalSpawnLocation.DEFAULT_RIFT_EXIT_POSITION, maxDepth);
    }

    /**
     * @param level    The level to iterate rooms in
     * @param startPos The position to iterate from
     * @return An iterator over the rooms in the level, up to 10 rooms deep from the provided position
     */
    public static Iterator<RoomRiftSpace> create(ServerLevel level, BlockPos startPos) {
        return create(level, startPos, DEFAULT_MAX_DEPTH);
    }

    /**
     * @param level    The level to iterate rooms in
     * @param startPos The section to iterate from
     * @return An iterator over the rooms in the level, up to 10 rooms deep from the provided position
     */
    public static Iterator<RoomRiftSpace> create(ServerLevel level, SectionPos startPos) {
        return create(level, startPos, DEFAULT_MAX_DEPTH);
    }

    /**
     * @param level    The level to iterate rooms in
     * @param startPos The position to iterate from
     * @param depth    The number of rooms outwards to iterate to
     * @return An iterator over the rooms in the level, from the provided position up to the provided depth of rooms
     *         away
     */
    public static Iterator<RoomRiftSpace> create(ServerLevel level, BlockPos startPos, int depth) {
        return create(level, SectionPos.of(startPos), depth);
    }

    /**
     * @param level    The level to iterate rooms in
     * @param startPos The position to iterate from
     * @param depth    The number of rooms outwards to iterate to
     * @return An iterator over the rooms in the level, from the provided position up to the provided depth of rooms
     *         away
     */
    public static Iterator<RoomRiftSpace> create(ServerLevel level, SectionPos startPos, int depth) {
        if (!(level.getChunkSource().getGenerator() instanceof FastRiftGenerator generator)) {
            return Collections.emptyIterator();
        }
        RiftLayout layout = generator.getOrCreateLayout(level.getServer());
        return new ConnectedRoomIterator(layout, startPos, depth);
    }

    @Override
    public boolean hasNext() {
        return !currentLayer.isEmpty();
    }

    @Override
    public RoomRiftSpace next() {
        RoomRiftSpace result = currentLayer.pop();
        if (depth < maxDepth) {
            result.corridors().forEach(corridor -> {
                RiftSpace adjSpace = layout.getChunkSpace(corridor.getConnectingPos(result));
                if (adjSpace instanceof RoomRiftSpace adjRoom && visitedRooms.add(adjSpace.origin())) {
                    nextLayer.add(adjRoom);
                }
            });
        }
        if (currentLayer.isEmpty()) {
            Deque<RoomRiftSpace> prev = currentLayer;
            currentLayer = nextLayer;
            nextLayer = prev;
            depth++;
        }
        return result;
    }
}
