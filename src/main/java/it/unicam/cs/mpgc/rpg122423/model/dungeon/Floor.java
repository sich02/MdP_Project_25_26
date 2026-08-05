package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.SpawnRoom;

import java.util.ArrayList;
import java.util.List;
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

    public List<Direction> getAvailableDoors() {
        List<Direction> availableDoors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            Coordinate adjacent = new Coordinate(
                    this.currentPosition.x() + direction.getDx(),
                    this.currentPosition.y() + direction.getDy()
            );
            if (this.rooms.containsKey(adjacent)) {
                availableDoors.add(direction);
            }
        }
        return availableDoors;
    }

    public void move(Direction direction) {
        this.currentPosition = new Coordinate(
                this.currentPosition.x() + direction.getDx(),
                this.currentPosition.y() + direction.getDy()
        );
    }

    public int  getFloorNumber() {return floorNumber;}
    public boolean isCleared() {return isCleared;}
    public void markAsCleared(){this.isCleared = true;}

    public Map<Coordinate, Room> getRooms() {return rooms;}
    public Coordinate getCurrentPosition() {return currentPosition;}
    public Room getCurrentRoom(){return rooms.get(currentPosition);}
}
