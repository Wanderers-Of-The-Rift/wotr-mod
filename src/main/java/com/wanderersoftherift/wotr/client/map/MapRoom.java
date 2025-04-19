package com.wanderersoftherift.wotr.client.map;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.wanderersoftherift.wotr.client.render.MapRenderer3D;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static com.wanderersoftherift.wotr.client.map.Utils3D.calculateVertices;
import static com.wanderersoftherift.wotr.client.map.Utils3D.projectPoint;

/**
 * Represents a room on the map Mainly used to make rendering work well without spaghetti code Contains the list of
 * cells that make up the room and are the building block of map, as well as the position and metadata of the room
 */
public class MapRoom {
    public int x, y, z;
    public int sizeX, sizeY, sizeZ; // sizes for the room cuboid !! don't forget to remove .1 from north and east when
                                    // rendering to make space for potential tunnels - maybe even make the size
                                    // configurable
    private final float TWEEN_TUNNEL_SIZE = 0.3f; // size of the tunnel between rooms - gets subtracted from room size
                                                  // when rendering
    public Vector3f pos1, pos2;
    public ArrayList<MapCell> cells = new ArrayList<>();
    private int effectFlags;

    // to solve the rendering the 1wide tunnels, on render, go through all the cells that have the possibility of having
    // tunnel and check their variable
    // TODO: move rendering from MapCell to here, basically rewrite MapCell

    public MapRoom(int x, int y, int z, int sizeX, int sizeY, int sizeZ, ArrayList<MapCell> cells) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.pos1 = new Vector3f(x, y, z);
        this.pos2 = new Vector3f(x + sizeX, y + sizeY, z + sizeZ);
        this.effectFlags = MapRoomEffects.getFlags(new MapRoomEffects.Flag[] { MapRoomEffects.Flag.DOTS });
        if (cells != null) this.cells.addAll(cells);
    }

    public MapRoom(int x, int y, int z, int sizeX, int sizeY, int sizeZ, ArrayList<MapCell> cells, int effectFlags) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.pos1 = new Vector3f(x, y, z);
        this.pos2 = new Vector3f(x + sizeX, y + sizeY, z + sizeZ);
        if (cells != null) this.cells.addAll(cells);
        this.effectFlags = effectFlags;
    }

    private final int[][] wireEdges = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 }, { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
            { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 } };

    /**
     * Adds the cube to the buffer for rendering, assumes DEBUG_LINES renderer is active
     * 
     * @param buffer
     * @param camera
     */
    public void renderWireframe(BufferBuilder buffer, com.wanderersoftherift.wotr.client.map.VirtualCamera camera,
            Vector2i mapPosition, Vector2i mapSize) {
        Vector3f pos2_sub = new Vector3f(pos2.x - TWEEN_TUNNEL_SIZE, pos2.y - TWEEN_TUNNEL_SIZE,
                pos2.z - TWEEN_TUNNEL_SIZE);
        float[][] vertices = calculateVertices(pos1, pos2_sub);

        int i = 0; // i is there to just color one edge so we can maintain direction better
        for (int[] edge : wireEdges) {
            Vector3f sProj = projectPoint(vertices[edge[0]][0], vertices[edge[0]][1], vertices[edge[0]][2], camera,
                    mapPosition, mapSize);
            Vector3f eProj = projectPoint(vertices[edge[1]][0], vertices[edge[1]][1], vertices[edge[1]][2], camera,
                    mapPosition, mapSize);

            if (i == 69) { // color one edge
                buffer.addVertex(sProj.x, sProj.y, sProj.z)
                        .setColor(1f, 0f, 0f, 1f)
                        .setUv(0.0f, 0.0f)
                        .misc(MapRenderer3D.EFFECTS, 0);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
                buffer.addVertex(eProj.x, eProj.y, eProj.z)
                        .setColor(1f, 0f, 0f, 1f)
                        .setUv(1.0f, 0.0f)
                        .misc(MapRenderer3D.EFFECTS, 0);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
            } else {
                buffer.addVertex(sProj.x, sProj.y, sProj.z)
                        .setColor(0f, 1f, 0f, .8f)
                        .setUv(0.0f, 0.0f)
                        .misc(MapRenderer3D.EFFECTS, 0);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
                buffer.addVertex(eProj.x, eProj.y, eProj.z)
                        .setColor(0f, 1f, 0f, .8f)
                        .setUv(1.0f, 0.0f)
                        .misc(MapRenderer3D.EFFECTS, 0);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
            }
            i++;
        }

    }

    private final int[][] cubeFaces = { { 0, 1, 2, 3 }, // bottom
            { 7, 6, 5, 4 }, // top
            { 4, 5, 1, 0 }, // front
            { 6, 7, 3, 2 }, // back
            { 0, 3, 7, 4 }, // left
            { 5, 6, 2, 1 } // right
    };

    /**
     * Adds the cube to the buffer for rendering, assumes QUADS renderer is active
     * 
     * @param buffer
     * @param camera
     */
    public void renderCube(BufferBuilder buffer, VirtualCamera camera, Vector4f color, Vector2i mapPosition,
            Vector2i mapSize) {
        Vector3f pos2_sub = new Vector3f(this.pos2.x - this.TWEEN_TUNNEL_SIZE, this.pos2.y - this.TWEEN_TUNNEL_SIZE,
                this.pos2.z - this.TWEEN_TUNNEL_SIZE);
        float[][] vertices = calculateVertices(this.pos1, pos2_sub);

        for (int[] face : this.cubeFaces) {
            Vector3f p1 = projectPoint(vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2], camera,
                    mapPosition, mapSize);
            Vector3f p2 = projectPoint(vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2], camera,
                    mapPosition, mapSize);
            Vector3f p3 = projectPoint(vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2], camera,
                    mapPosition, mapSize);
            Vector3f p4 = projectPoint(vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2], camera,
                    mapPosition, mapSize);

            buffer.addVertex(p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 0.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
            buffer.addVertex(p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 0.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
            buffer.addVertex(p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 1.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
            buffer.addVertex(p4.x, p4.y, p4.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 1.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
        }

        // tunnels
        this.cells.stream().filter(this::shouldCheckTunnelPredicate).forEach((cell) -> {
            if (cell.connections.contains(Direction.EAST)) {
                // draw East tunnel
                cell.renderEastConnection(this.TWEEN_TUNNEL_SIZE, buffer, camera, new Vector4f(0.2f, 0.2f, 0.2f, .4f),
                        mapPosition, mapSize);
            }
            if (cell.connections.contains(Direction.NORTH)) {
                // draw North tunnel
                cell.renderNorthConnection(this.TWEEN_TUNNEL_SIZE, buffer, camera, new Vector4f(0.2f, 0.2f, 0.2f, .4f),
                        mapPosition, mapSize);
            }
            if (cell.connections.contains(Direction.UP)) {
            }
        });
    }

    private boolean shouldCheckTunnelPredicate(MapCell cell) {
        return cell.pos1.x == this.x + this.sizeX - 1 || // East wall
                cell.pos1.y == this.y + this.sizeY - 1 || // Top of the room
                cell.pos1.z == this.z + this.sizeZ - 1; // North wall
    }

}
