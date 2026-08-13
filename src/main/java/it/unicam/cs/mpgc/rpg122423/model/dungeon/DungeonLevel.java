package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;

import java.util.Map;

public class DungeonLevel {
    private final Floor floor;
    private final Map<Coordinate, Room> layout;
    private Coordinate currentPosition;

    public DungeonLevel(Floor floor, Coordinate startingPosition) {
        this.floor = floor;
        this.layout = floor.getRooms();
        this.currentPosition = startingPosition;
    }

    public Floor getFloor() {
        return floor;
    }

    public Room getCurrentRoom() {
        return layout.get(currentPosition);
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public Room getRoomAt(Coordinate coordinate) {
        return layout.get(coordinate);
    }

    public boolean hasDoor(Coordinate pos, Direction dir) {
        return layout.containsKey(pos.moveTo(dir));
    }

    public boolean movePlayer(Direction dir) {
        Coordinate nextPos = currentPosition.moveTo(dir);
        if (layout.containsKey(nextPos)) {
            currentPosition = nextPos;
            return true;
        }
        return false;
    }
}