package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Navigator {
    private final Floor floor;
    private Coordinate currentPosition;

    public Navigator(Floor floor) {
        this.floor = Objects.requireNonNull(floor);
        this.currentPosition = floor.getStartingCoordinate();
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public Room getCurrentRoom() {
        return floor.getRoomAt(currentPosition)
                .orElseThrow(() -> new IllegalStateException(
                        "Incongruenza topologica: Nessuna stanza trovata alla posizione " + currentPosition
                ));
    }

    public List<Direction> getAvailableDoors() {
        List<Direction> availableDoors = new ArrayList<>();

        if (!getCurrentRoom().isCleared()) {
            return availableDoors;
        }

        for (Direction direction : Direction.values()) {
            Coordinate adjacent = this.currentPosition.moveTo(direction);
            if (floor.getRoomAt(adjacent).isPresent()) {
                availableDoors.add(direction);
            }
        }
        return availableDoors;
    }

    public void move(Direction direction) {
        if (!getAvailableDoors().contains(direction)) {
            throw new IllegalArgumentException("Movimento bloccato o direzione non valida: " + direction);
        }

        this.currentPosition = this.currentPosition.moveTo(direction);
    }
}