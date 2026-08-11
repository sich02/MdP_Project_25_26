package it.unicam.cs.mpgc.rpg122423.model.dungeon;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.SpawnRoom;

import java.util.Map;
import java.util.Optional;

public class Floor {
    private final int floorNumber;
    private final Map<Coordinate, Room> rooms;
    private boolean cleared;

    public Floor(int floorNumber, Map<Coordinate, Room> rooms) {
        if (floorNumber <= 0) {
            throw new IllegalArgumentException("Il numero del piano deve essere maggiore di 0");
        }

        this.floorNumber = floorNumber;
        this.rooms = rooms;
        this.cleared = false;
        this.rooms.put(new Coordinate(0, 0), new SpawnRoom());
    }

    public Optional<Room> getRoomAt(Coordinate coordinate) {
        return Optional.ofNullable(rooms.get(coordinate));
    }

    public Coordinate getStartingCoordinate() {return new Coordinate(0, 0);}
    public int getFloorNumber() {return floorNumber;}
    public boolean isCleared() {return cleared;}
    public void markAsCleared() {this.cleared = true;}
    public Map<Coordinate, Room> getRooms() {return java.util.Collections.unmodifiableMap(rooms);}


}