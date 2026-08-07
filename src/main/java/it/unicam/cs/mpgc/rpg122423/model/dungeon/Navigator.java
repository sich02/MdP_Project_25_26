package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Lockable;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    private final Floor currentFloor;
    private Coordinate currentPosition;

    public Navigator(Floor currentFloor) {
        this.currentFloor = currentFloor;
        this.currentPosition = new Coordinate(0, 0);
    }

    public boolean isDoorLocked(Direction direction) {
        Coordinate targetCoordinate = direction.applyTo(currentPosition);

        // Risolve l'errore Optional: usiamo orElse(null) per estrarre il valore
        Room targetRoom = currentFloor.getRoomAt(targetCoordinate).orElse(null);

        return targetRoom instanceof Lockable targetLockable && targetLockable.isLocked();
    }

    public void unlockDoor(Direction direction) {
        Coordinate targetCoordinate = direction.applyTo(currentPosition);
        Room targetRoom = currentFloor.getRoomAt(targetCoordinate).orElse(null);

        if (targetRoom instanceof Lockable targetLockable) {
            targetLockable.unlock();
        }
    }

    public boolean move(Direction direction) {
        Room currentRoom = currentFloor.getRoomAt(currentPosition).orElse(null);

        // Controllo di sicurezza aggiunto per l'Optional spacchettato
        if (currentRoom == null || !currentRoom.isCleared()) return false;

        Coordinate targetCoordinate = direction.applyTo(currentPosition);
        Room targetRoom = currentFloor.getRoomAt(targetCoordinate).orElse(null);

        if (targetRoom == null || isDoorLocked(direction)) return false;

        this.currentPosition = targetCoordinate;
        return true;
    }

    /**
     * Restituisce la lista delle direzioni in cui esiste una stanza adiacente
     * raggiungibile dalla posizione corrente.
     */
    public List<Direction> getAvailableDoors() {
        List<Direction> availableDoors = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Coordinate targetCoordinate = direction.applyTo(currentPosition);
            if (currentFloor.getRoomAt(targetCoordinate).isPresent()) {
                availableDoors.add(direction);
            }
        }

        return availableDoors;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public Room getCurrentRoom() {
        return currentFloor.getRoomAt(currentPosition).orElse(null);
    }
}