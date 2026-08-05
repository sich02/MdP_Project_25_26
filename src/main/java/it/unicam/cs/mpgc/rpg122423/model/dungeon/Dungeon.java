package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.FloorGenerator;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;

import java.util.Map;

public class Dungeon {
    private final FloorGenerator floorGenerator;
    private Floor currentFloor;
    private int currentFloorNumber;

    public Dungeon() {
        this.floorGenerator = new FloorGenerator();
        this.currentFloorNumber = 1;
        this.currentFloor = generateAndLoadFloor(this.currentFloorNumber);
    }

    public boolean advanceToNextFloor() {
        if (!currentFloor.isCleared()) {
            return false;
        }
        currentFloorNumber++;
        currentFloor = generateAndLoadFloor(currentFloorNumber);
        return true;
    }

    private Floor generateAndLoadFloor(int floorNumber) {
        Map<Coordinate, Room> layout = floorGenerator.generateFloor(floorNumber);
        return new Floor(floorNumber, layout);
    }


    public Floor getCurrentFloor() {return currentFloor;}
    public int getCurrentFloorNumber() {return currentFloorNumber;}
}