package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;

import java.util.Map;

public class DungeonLevel {
    private final Map<Coordinate, Room> layout;
    private Coordinate currentPosition;

    public DungeonLevel(Map<Coordinate, Room> layout, Coordinate startingPosition) {
        this.layout = layout;
        this.currentPosition = startingPosition;
    }

    public Room getCurrentRoom() {
        return layout.get(currentPosition);
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Verifica se c'è una stanza adiacente nella direzione indicata (quindi una porta).
     */
    public boolean hasDoor(Coordinate pos, Direction dir) {
        return layout.containsKey(pos.moveTo(dir));
    }

    /**
     * Tenta di muovere il giocatore usando il tuo moveTo().
     */
    public boolean movePlayer(Direction dir) {
        Coordinate nextPos = currentPosition.moveTo(dir);

        // Se la mappa contiene una stanza in quella coordinata, lo spostamento è valido
        if (layout.containsKey(nextPos)) {
            currentPosition = nextPos;
            return true;
        }

        return false;
    }
}