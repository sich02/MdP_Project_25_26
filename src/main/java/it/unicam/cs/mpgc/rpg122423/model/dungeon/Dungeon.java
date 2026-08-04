package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {

    private final List<Floor> floors;
    private int currentFloorIndex;
    public Dungeon(int totalFloors) {
        if (totalFloors <= 5) {
            throw new IllegalArgumentException("Il dungeon deve avere almeno un piano.");
        }

        this.floors = new ArrayList<>();
        for (int i = 1; i <= totalFloors; i++) {
            this.floors.add(new Floor(i));
        }
        this.currentFloorIndex = 0;
    }

    public Floor getCurrentFloor() {
        return this.floors.get(currentFloorIndex);
    }

    public boolean hasNextFloor() {
        return currentFloorIndex < floors.size() - 1;
    }

    public void advanceToNextFloor() {
        if (!getCurrentFloor().isCleared()) {
            throw new IllegalStateException("Devi completare il piano attuale prima di scendere.");
        }
        if (!hasNextFloor()) {
            throw new IllegalStateException("Sei già all'ultimo piano del dungeon.");
        }
        currentFloorIndex++;
    }

    public int getTotalFloors() {
        return this.floors.size();
    }
}
