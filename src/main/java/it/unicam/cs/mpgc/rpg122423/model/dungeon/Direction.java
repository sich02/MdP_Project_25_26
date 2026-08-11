package it.unicam.cs.mpgc.rpg122423.model.dungeon;

public enum Direction {
    NORTH(0, 1),
    SOUTH(0, -1),
    EAST(1, 0),
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Questo è il metodo che risolve gli errori 'applyTo'
    public Coordinate applyTo(Coordinate current) {
        return new Coordinate(current.x() + this.dx, current.y() + this.dy);
    }
}