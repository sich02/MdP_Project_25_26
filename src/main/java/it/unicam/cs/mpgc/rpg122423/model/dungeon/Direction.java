package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;

public enum Direction {
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Coordinate applyTo(Coordinate origin){
        return new Coordinate(origin.x()+dx, origin.y()+dy);
    }
}
