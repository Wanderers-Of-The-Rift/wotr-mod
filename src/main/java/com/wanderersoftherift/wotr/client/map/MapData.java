package com.wanderersoftherift.wotr.client.map;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * This class contains all map data and methods for manipulating it Rendering is separated into client.render package
 * Mostly empty for now, will contain player data as well
 *
 * MapCells are stored in two lists/arrays/hashmaps/whatever is decided: 1. a list of all cells for easier granular
 * processing (mainly for 1wide tunnels between rooms) 2. a list inside each MapRoom for rendering
 */
public class MapData {
    public static HashMap<Vector3i, MapCell> cells = new HashMap<>();
    public static HashMap<Vector3i, MapRoom> rooms = new HashMap<>();

    static {
        int size = 6;
        MapCell cell3 = new MapCell(new Vector3f(-3,1,-3), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
        ArrayList<MapCell> new_cells3 = new ArrayList<>();
        new_cells3.add(cell3);
        addRoom(new MapRoom(-3, 1, -3, 1, 1, 1, new_cells3));
        MapCell cell4 = new MapCell(new Vector3f(-3,-1,-3), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
        ArrayList<MapCell> new_cells4 = new ArrayList<>();
        new_cells4.add(cell4);
        addRoom(new MapRoom(-3, -1, -3, 1, 1, 1, new_cells4));
        for (int x = -size/2; x <= size/2; x++) {
            for (int y = -size/2; y <= size/2; y++) {
                MapCell cell = new MapCell(new Vector3f(x,0,y), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
                ArrayList<MapCell> new_cells = new ArrayList<>();
                new_cells.add(cell);
                addRoom(new MapRoom(x, 0, y, 1, 1, 1, new_cells));
            }
        }
        MapCell cell = new MapCell(new Vector3f(-3,0,-4), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
        ArrayList<MapCell> new_cells = new ArrayList<>();
        new_cells.add(cell);
        addRoom(new MapRoom(-3, 0, -4, 1, 1, 1, new_cells));
        MapCell cell2 = new MapCell(new Vector3f(-4,0,-3), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
        ArrayList<MapCell> new_cells2 = new ArrayList<>();
        new_cells2.add(cell2);
        addRoom(new MapRoom(-4, 0, -3, 1, 1, 1, new_cells2));

        MapCell cella = new MapCell(new Vector3f(4, 0, 0), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
        MapCell cellb = new MapCell(new Vector3f(4, 0, 2), 1, 0, EnumSet.allOf(Direction.class), EnumSet.noneOf(Direction.class));
        ArrayList<MapCell> new_cellse = new ArrayList<>();
        new_cellse.add(cella);
        new_cellse.add(cellb);
        addRoom(new MapRoom(4, -1, 0, 3, 3, 3, new_cellse));
    }

    /**
     * Used to add new cell to the map In the future will be used to process cell changes like the small 1wide tunnels
     * between rooms
     *
     * @param cell
     */
    public static void addCell(MapCell cell) {
        cells.put(new Vector3i((int) cell.pos1.x, (int) cell.pos1.y, (int) cell.pos1.z), cell);
    }

    /**
     * Used to add new room if the room cells are empty, generates all cells for the room otherwise the cells should be
     * defined in their entirity, otherwise the 1wide tunnel autoprocessing will not work properly
     *
     * @param room
     */
    public static void addRoom(MapRoom room) {
        // check if the room is already in the map
        if (rooms.containsKey(new Vector3i(room.x, room.y, room.z))) { // remove room if already exists to properly update everything
            oldRoom = rooms.get(new Vector3i(room.x, room.y, room.z));
            removeRoom(oldRoom);
        }
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
        checkRoomConnections(room);
    }

    /**
     * Used to remove a room from the map, including cells from the cell list
     *
     * @param room
     */
    public static void removeRoom(MapRoom room) {
        rooms.remove(new Vector3i(room.x, room.y, room.z));
        room.cells.forEach(cell -> cells.remove(new Vector3i((int) cell.pos1.x, (int) cell.pos1.y, (int) cell.pos1.z)));
    }

    public static void reset() {
        cells.clear();
        rooms.clear();
    }

    public static void checkRoomConnections(MapRoom room) {
        room.getPotentialTunnels().forEach(cell -> {
            int x = (int) cell.pos1.x;
            int y = (int) cell.pos1.y;
            int z = (int) cell.pos1.z;

            Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};
            Vector3i[] offsets = {new Vector3i(0, 0, 1), new Vector3i(0, 0, -1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, 0), new Vector3i(0, 1, 0), new Vector3i(0, -1, 0)};
            Direction[] opposites = {Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP};

            for (int i = 0; i < directions.length; i++) {
                if (cell.openings.contains(directions[i])) {
                    Vector3i neighborPos = new Vector3i(x, y, z).add(offsets[i]);
                    MapCell neighbor = cells.get(neighborPos);
                    if (neighbor != null && neighbor.openings.contains(opposites[i])) {
                        if (directions[i] == Direction.SOUTH || directions[i] == Direction.WEST || directions[i] == Direction.DOWN) {
                            neighbor.connections.add(opposites[i]);
                        } else {
                            cell.connections.add(directions[i]);
                        }
                    }
                }
            }
        });
    }
}
