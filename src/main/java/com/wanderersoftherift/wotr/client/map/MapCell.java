package com.wanderersoftherift.wotr.client.map;


import com.mojang.blaze3d.vertex.BufferBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.render.MapRenderer3D;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.EnumSet;

import static com.wanderersoftherift.wotr.client.map.Utils3D.calculateVertices;
import static com.wanderersoftherift.wotr.client.map.Utils3D.projectPoint;

/**
 * Represents a single cell in the map
 * Contains the position and type of the cell
 */
public class MapCell {
    public Vector3f pos1; // should only be used for rendering,
    private Vector3f pos2;
    // connection is 1wide tunnel between rooms
    public EnumSet<Direction> connections = EnumSet.noneOf(Direction.class); // used for rendering, only takes action on North, East, Top
    public EnumSet<Direction> openings = EnumSet.noneOf(Direction.class); // used for cell connection solver
    // for solver: on horizontals only one opening creates a connection regardless of the other room acceptance
    // on verticals, both rooms need to "accept" the connection
    int x, y, z;
    private int type;
    // add rendering for connections inside here (probably), might be inside MapRoom as well
    private int effectFlags;

    public MapCell(Vector3f loc, float size, int type) {
        this.pos1 = loc;
        this.pos2 = new Vector3f(loc.x + size, loc.y + size, loc.z + size);
        this.type = type;
        this.effectFlags = 0;
    }

    public MapCell(Vector3f loc, float size, int type, EnumSet<Direction> openings, EnumSet<Direction> connections) {
        this.pos1 = loc;
        this.pos2 = new Vector3f(loc.x + size, loc.y + size, loc.z + size);
        this.type = type;
        this.openings = openings;
        this.connections = connections;
    }

    public MapCell(Vector3f pos1, Vector3f pos2, int type) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public int getType() {
        return type;
    }

    public MapCell setEffects(int effectFlags) {
        this.effectFlags = effectFlags;
        return this;
    }

    /**
     * Adds the cube to the buffer for rendering, assumes DEBUG_LINES renderer is active
     * @param buffer
     * @param camera
     */
    public void renderWireframe(BufferBuilder buffer, com.wanderersoftherift.wotr.client.map.VirtualCamera camera, Vector2i mapPosition, Vector2i mapSize) {
        float[][] vertices = calculateVertices(pos1, pos2);

        int[][] edges = {
                { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
                { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
                { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
        };

        int i = 0; // i is there to just color one edge so we can maintain direction better
        for (int[] edge : edges) {
            Vector4f start = new Vector4f(vertices[edge[0]][0], vertices[edge[0]][1], vertices[edge[0]][2], 1.0f);
            Vector4f end = new Vector4f(vertices[edge[1]][0], vertices[edge[1]][1], vertices[edge[1]][2], 1.0f);

            Vector3f sProj = projectPoint(new Vector3f(start.x, start.y, start.z), camera, mapPosition, mapSize);
            Vector3f eProj = projectPoint(new Vector3f(end.x, end.y, end.z), camera, mapPosition, mapSize);

            if (i==69) { // color one edge
                buffer.addVertex(sProj.x, sProj.y, sProj.z)
                        .setColor(1f, 0f, 0f, 1f).setUv(0.0f, 0.0f);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
                buffer.addVertex(eProj.x, eProj.y, eProj.z)
                        .setColor(1f, 0f, 0f, 1f).setUv(1.0f, 0.0f);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
            } else {
                buffer.addVertex(sProj.x, sProj.y, sProj.z)
                        .setColor(1f, 1f, 1f, 1f).setUv(0.0f, 0.0f);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
                buffer.addVertex(eProj.x, eProj.y, eProj.z)
                        .setColor(1f, 1f, 1f, 1f).setUv(1.0f, 0.0f);
                MapRenderer3D.putEffects(this.effectFlags, buffer);
            }
            i++;
        }
    }

    /**
     * Adds the cube to the buffer for rendering, assumes QUADS renderer is active
     * @param buffer
     * @param camera
     */
    public void renderCube(BufferBuilder buffer, VirtualCamera camera, Vector4f color, Vector2i mapPosition, Vector2i mapSize) {
        float[][] vertices = calculateVertices(pos1, pos2);

        int[][] faces = {
                {0, 1, 2, 3}, // bottom
                {7, 6, 5, 4}, // top
                {4, 5, 1, 0}, // front
                {6, 7, 3, 2}, // back
                {0, 3, 7, 4}, // left
                {5, 6, 2, 1}  // right
        };

        for (int[] face : faces) {
            Vector4f v1 = new Vector4f(vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2], 1.0f);
            Vector4f v2 = new Vector4f(vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2], 1.0f);
            Vector4f v3 = new Vector4f(vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2], 1.0f);
            Vector4f v4 = new Vector4f(vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2], 1.0f);

            Vector3f p1 = projectPoint(new Vector3f(v1.x, v1.y, v1.z), camera, mapPosition, mapSize);
            Vector3f p2 = projectPoint(new Vector3f(v2.x, v2.y, v2.z), camera, mapPosition, mapSize);
            Vector3f p3 = projectPoint(new Vector3f(v3.x, v3.y, v3.z), camera, mapPosition, mapSize);
            Vector3f p4 = projectPoint(new Vector3f(v4.x, v4.y, v4.z), camera, mapPosition, mapSize);

            buffer.addVertex(p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 0.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
            buffer.addVertex(p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 0.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
            buffer.addVertex(p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 1.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
            buffer.addVertex(p4.x, p4.y, p4.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 1.0f);
            MapRenderer3D.putEffects(this.effectFlags, buffer);
        }
    }

    private final float TWEEN_TUNNEL_SIZE = 0.1f;
    public void renderEastConnection(float length, BufferBuilder buffer, VirtualCamera camera, Vector4f color, Vector2i mapPosition, Vector2i mapSize) {
        float cellSize = 1 - length;
        // render a connection to the north
        Vector3f pp1 = new Vector3f(this.pos1.x + cellSize, this.pos1.y + cellSize/2- this.TWEEN_TUNNEL_SIZE, this.pos1.z+cellSize/2-this.TWEEN_TUNNEL_SIZE);
        Vector3f pp2 = new Vector3f(this.pos1.x + cellSize + length, this.pos1.y + cellSize/2 + this.TWEEN_TUNNEL_SIZE, this.pos1.z + cellSize/2 + this.TWEEN_TUNNEL_SIZE);

        float[][] vertices = calculateVertices(pp1, pp2);

        int[][] faces = {
                {0, 1, 2, 3}, // bottom
                {7, 6, 5, 4}, // top
                {4, 5, 1, 0}, // front
                {6, 7, 3, 2}, // back
                {0, 3, 7, 4}, // left
                {5, 6, 2, 1}  // right
        };

        for (int[] face : faces) {
            Vector4f v1 = new Vector4f(vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2], 1.0f);
            Vector4f v2 = new Vector4f(vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2], 1.0f);
            Vector4f v3 = new Vector4f(vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2], 1.0f);
            Vector4f v4 = new Vector4f(vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2], 1.0f);

            Vector3f p1 = projectPoint(new Vector3f(v1.x, v1.y, v1.z), camera, mapPosition, mapSize);
            Vector3f p2 = projectPoint(new Vector3f(v2.x, v2.y, v2.z), camera, mapPosition, mapSize);
            Vector3f p3 = projectPoint(new Vector3f(v3.x, v3.y, v3.z), camera, mapPosition, mapSize);
            Vector3f p4 = projectPoint(new Vector3f(v4.x, v4.y, v4.z), camera, mapPosition, mapSize);

            buffer.addVertex(p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 0.0f);
            MapRenderer3D.putEffects(0, buffer);
            buffer.addVertex(p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 0.0f);
            MapRenderer3D.putEffects(0, buffer);
            buffer.addVertex(p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 1.0f);
            MapRenderer3D.putEffects(0, buffer);
            buffer.addVertex(p4.x, p4.y, p4.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 1.0f);
            MapRenderer3D.putEffects(0, buffer);
        }
    }

    public void renderNorthConnection(float length, BufferBuilder buffer, VirtualCamera camera, Vector4f color, Vector2i mapPosition, Vector2i mapSize) {
        float cellSize = 1 - length;
        // render a connection to the north
        Vector3f pp1 = new Vector3f(this.pos1.x + cellSize/2 - this.TWEEN_TUNNEL_SIZE, this.pos1.y + cellSize/2- this.TWEEN_TUNNEL_SIZE, this.pos1.z+cellSize);
        Vector3f pp2 = new Vector3f(this.pos1.x + cellSize/2 + this.TWEEN_TUNNEL_SIZE, this.pos1.y + cellSize/2 + this.TWEEN_TUNNEL_SIZE, this.pos1.z + cellSize + length);

        float[][] vertices = calculateVertices(pp1, pp2);

        int[][] faces = {
                {0, 1, 2, 3}, // bottom
                {7, 6, 5, 4}, // top
                {4, 5, 1, 0}, // front
                {6, 7, 3, 2}, // back
                {0, 3, 7, 4}, // left
                {5, 6, 2, 1}  // right
        };

        for (int[] face : faces) {
            Vector4f v1 = new Vector4f(vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2], 1.0f);
            Vector4f v2 = new Vector4f(vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2], 1.0f);
            Vector4f v3 = new Vector4f(vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2], 1.0f);
            Vector4f v4 = new Vector4f(vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2], 1.0f);

            Vector3f p1 = projectPoint(new Vector3f(v1.x, v1.y, v1.z), camera, mapPosition, mapSize);
            Vector3f p2 = projectPoint(new Vector3f(v2.x, v2.y, v2.z), camera, mapPosition, mapSize);
            Vector3f p3 = projectPoint(new Vector3f(v3.x, v3.y, v3.z), camera, mapPosition, mapSize);
            Vector3f p4 = projectPoint(new Vector3f(v4.x, v4.y, v4.z), camera, mapPosition, mapSize);

            buffer.addVertex(p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 0.0f);
            MapRenderer3D.putEffects(0, buffer);
            buffer.addVertex(p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 0.0f);
            MapRenderer3D.putEffects(0, buffer);
            buffer.addVertex(p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, color.w).setUv(1.0f, 1.0f);
            MapRenderer3D.putEffects(0, buffer);
            buffer.addVertex(p4.x, p4.y, p4.z).setColor(color.x, color.y, color.z, color.w).setUv(0.0f, 1.0f);
            MapRenderer3D.putEffects(0, buffer);
        }
    }

}
