package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.CombatRoom;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.SpawnRoom;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void testFloorInitializationAndSilentSpawnCreation() {
        Map<Coordinate, Room> emptyMap = new HashMap<>();
        Floor floor = new Floor(1, emptyMap);

        assertEquals(1, floor.getFloorNumber());
        assertFalse(floor.isCleared());
        assertEquals(new Coordinate(0, 0), floor.getCurrentPosition());
        assertInstanceOf(SpawnRoom.class, floor.getCurrentRoom(), "Deve creare una SpawnRoom in 0,0 se assente.");
        assertEquals(1, floor.getRooms().size());
    }

    @Test
    void testSilentSpawnOverwrite() {
        Map<Coordinate, Room> map = new HashMap<>();
        map.put(new Coordinate(0, 0), new CombatRoom(true));
        Floor floor = new Floor(1, map);

        assertInstanceOf(SpawnRoom.class, floor.getCurrentRoom(), "Eventuali stanze errate in 0,0 devono essere sovrascritte forzatamente da una SpawnRoom.");
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
}