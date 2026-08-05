package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.SpawnRoom;

import java.util.Map;

public class Floor {
    private final int floorNumber;
    private boolean isCleared;
    private final Map<Coordinate, Room> rooms;
    private Coordinate currentPosition;

    public Floor(int floorNumber, Map<Coordinate, Room> rooms) {
        if(floorNumber <=0){
            throw new IllegalArgumentException("Il numero del piano non puó essere minore di 0");
        }
        this.floorNumber = floorNumber;
        this.isCleared = false;
        this.rooms = rooms;

        this.rooms.put(new Coordinate(0, 0), new SpawnRoom());
        this.currentPosition = new Coordinate(0, 0);
    }

    public int  getFloorNumber() {return floorNumber;}
    public boolean isCleared() {return isCleared;}
    public void markAsCleared(){this.isCleared = true;}

    public Map<Coordinate, Room> getRooms() {return rooms;}
    public Coordinate getCurrentPosition() {return currentPosition;}
    public Room getCurrentRoom(){return rooms.get(currentPosition);}
}
