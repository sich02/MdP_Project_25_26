package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void testFloorInitializationAndSilentSpawnCreation() {
        Map<Coordinate, Room> emptyMap = new HashMap<>();
        Floor floor = new Floor(1, emptyMap);
        Navigator navigator = new Navigator(floor);

        assertEquals(1, floor.getFloorNumber());
        assertFalse(floor.isCleared());
        assertEquals(new Coordinate(0, 0), navigator.getCurrentPosition());
        assertInstanceOf(SpawnRoom.class, navigator.getCurrentRoom(), "Deve creare una SpawnRoom in 0,0 se assente.");
        assertEquals(1, floor.getRooms().size());
    }

    @Test
    void testSilentSpawnOverwrite() {
        Map<Coordinate, Room> map = new HashMap<>();
        map.put(new Coordinate(0, 0), new CombatRoom(true));
        Floor floor = new Floor(1, map);
        Navigator navigator = new Navigator(floor);

        assertInstanceOf(SpawnRoom.class, navigator.getCurrentRoom(), "Eventuali stanze errate in 0,0 devono essere sovrascritte forzatamente da una SpawnRoom.");
    }

    @Test
    void testInvalidFloorNumberThrowsException() {
        Map<Coordinate, Room> map = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> new Floor(0, map), "Un piano 0 deve lanciare eccezione.");
        assertThrows(IllegalArgumentException.class, () -> new Floor(-1, map), "Un piano negativo deve lanciare eccezione.");
    }

    @Test
    void testMarkAsCleared() {
        Floor floor = new Floor(1, new HashMap<>());

        assertFalse(floor.isCleared(), "Il piano non deve essere completato all'inizializzazione.");

        floor.markAsCleared();

        assertTrue(floor.isCleared(), "Il piano deve risultare completato dopo la chiamata a markAsCleared.");
    }

    @Test
    void testMovementAndAvailableDoors() {
        Map<Coordinate, Room> map = new HashMap<>();
        map.put(new Coordinate(0, 0), new SpawnRoom());
        map.put(new Coordinate(0, 1), new BossRoom());

        Floor floor = new Floor(1, map);
        Navigator navigator = new Navigator(floor);
        List<Direction> doors = navigator.getAvailableDoors();

        assertEquals(1, doors.size());
        assertTrue(doors.contains(Direction.NORTH));

        navigator.move(Direction.NORTH);

        assertEquals(new Coordinate(0, 1), navigator.getCurrentPosition());

        navigator.getCurrentRoom().markAsCleared();
        List<Direction> doorsFromNewRoom = navigator.getAvailableDoors();

        assertEquals(1, doorsFromNewRoom.size());
        assertTrue(doorsFromNewRoom.contains(Direction.SOUTH));
    }
}