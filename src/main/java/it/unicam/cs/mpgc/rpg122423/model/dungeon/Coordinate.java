package it.unicam.cs.mpgc.rpg122423.model.dungeon;

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

    public Coordinate moveTo(Direction direction) {
        return direction.applyTo(this);
    }

    /**
     * Conta quanti vicini di questa coordinata sono presenti nel layout fornito.
     */
    public int countNeighborsIn(java.util.Set<Coordinate> layout) {
        int count = 0;
        for (Coordinate neighbor : getNeighbors()) {
            if (layout.contains(neighbor)) count++;
        }
        return count;
    }
}
