package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.SpawnRoom;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void testGetRoomAtReturnsOptionalWithRoom() {
        Map<Coordinate, Room> layout = new HashMap<>();
        Coordinate testCoord = new Coordinate(1, 0);
        Room dummyRoom = new DummyRoom(true);
        layout.put(testCoord, dummyRoom);
        Floor floor = new Floor(1, layout, 12345L);
        Optional<Room> retrievedRoom = floor.getRoomAt(testCoord);

        assertTrue(retrievedRoom.isPresent(), "La stanza dovrebbe essere presente alle coordinate " + testCoord);
        assertEquals(dummyRoom, retrievedRoom.get(), "La stanza estratta deve essere esattamente quella inserita nel layout");
    }

    @Test
    void testGetRoomAtReturnsEmptyOptionalForInvalidCoordinate() {
        Map<Coordinate, Room> layout = new HashMap<>();
        Floor floor = new Floor(1, layout, 12345L);
        Optional<Room> emptyResult = floor.getRoomAt(new Coordinate(99, 99));

        assertTrue(emptyResult.isEmpty(), "Richiedere coordinate vuote deve restituire un Optional.empty()");
    }

    @Test
    void testFloorIsClearedOnlyWhenMarked() {
        Map<Coordinate, Room> layout = new HashMap<>();
        Floor floor = new Floor(1, layout, 12345L);

        assertFalse(floor.isCleared(), "Appena creato, il piano non deve risultare completato");

        floor.markAsCleared();

        assertTrue(floor.isCleared(), "Il piano deve risultare completato dopo aver chiamato markAsCleared()");
    }

    @Test
    void testConstructorForcesSpawnRoomAtOrigin() {
        Map<Coordinate, Room> layout = new HashMap<>();
        Floor floor = new Floor(1, layout, 12345L);

        Optional<Room> spawnResult = floor.getRoomAt(new Coordinate(0, 0));

        assertTrue(spawnResult.isPresent(), "Deve sempre esistere una stanza a (0,0)");
        assertInstanceOf(SpawnRoom.class, spawnResult.get(), "La stanza a (0,0) deve essere forzatamente una SpawnRoom");
    }

    private static class DummyRoom implements Room {
        private boolean isCleared;

        public DummyRoom(boolean isCleared) {
            this.isCleared = isCleared;
        }

        @Override
        public boolean isCleared() {
            return this.isCleared;
        }

        @Override
        public void markAsCleared() {
            this.isCleared = true;
        }

        @Override
        public String getRoomType() {
            return "NORMAL";
        }
    }
}