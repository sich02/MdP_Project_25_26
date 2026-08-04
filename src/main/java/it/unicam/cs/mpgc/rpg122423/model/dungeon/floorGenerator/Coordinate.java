package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import java.util.List;

public record Coordinate(int x, int y) {
    public List<Coordinate> getNeighbors() {
        return List.of(
                new Coordinate(x, y + 1),
                new Coordinate(x, y - 1),
                new Coordinate(x + 1, y),
                new Coordinate(x - 1, y)
        );
    }
}
