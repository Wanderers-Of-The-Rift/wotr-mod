package com.wanderersoftherift.wotr.client.map;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * This class contains all map data and methods for manipulating it
 * Rendering is separated into client.render package
 * Mostly empty for now, will contain player data as well
 *
 * MapCells are stored in two lists/arrays/hashmaps/whatever is decided:
 * 1. a list of all cells for easier granular processing (mainly for 1wide tunnels between rooms)
 * 2. a list inside each MapRoom for rendering
 */
public class MapData {
    public static HashMap<Vector3i, MapCell> cells = new HashMap<>();
    public static HashMap<Vector3i, MapRoom> rooms = new HashMap<>();

    static {
        int size = 6;
        for (int x = -size/2; x <= size/2; x++) {
            for (int y = -size/2; y <= size/2; y++) {
                MapCell cell = new MapCell(new Vector3f(x,0,y), 1, 0, EnumSet.noneOf(Direction.class), EnumSet.of(Direction.NORTH, Direction.EAST, Direction.UP));
                ArrayList<MapCell> new_cells = new ArrayList<>();
                new_cells.add(cell);
                addRoom(new MapRoom(x, 0, y, 1, 1, 1, new_cells));
            }
        }
    }

    /**
     * Used to add new cell to the map
     * In the future will be used to process cell changes like the small 1wide tunnels between rooms
     * @param cell
     */
    public static void addCell(MapCell cell) {
        cells.put(new Vector3i((int) cell.pos1.x, (int) cell.pos1.y, (int) cell.pos1.z), cell);
    }

    /**
     * Used to add new room
     * if the room cells are empty, generates all cells for the room
     * otherwise the cells should be defined in their entirity, otherwise the 1wide tunnel autoprocessing will not work properly
     * @param room
     */
    public static void addRoom(MapRoom room) {
        if (room.cells == null) { // if there are no cells, create them
            room.cells = new java.util.ArrayList<>();
            for (int x = room.x; x < room.x + room.sizeX; x++) {
                for (int y = room.y; y < room.y + room.sizeY; y++) {
                    for (int z = room.z; z < room.z + room.sizeZ; z++) {
                        MapCell cell = new MapCell(new Vector3f(x, y, z), 1, 0);
                        room.cells.add(cell);
                        cells.put(new Vector3i(x, y, z), cell);
                    }
                }
            }
        } else {
            room.cells.forEach(cell -> cells.put(new Vector3i((int) cell.pos1.x, (int) cell.pos1.y, (int) cell.pos1.z), cell)); // add all cells to hashmap
        }
        rooms.put(new Vector3i(room.x, room.y, room.z), room); // add the actual room to hashmap
    }

    public static void reset() {
        cells.clear();
    }
}
