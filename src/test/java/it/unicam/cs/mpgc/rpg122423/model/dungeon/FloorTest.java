package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void testGetRoomAtReturnsOptionalWithRoom() {
        Map<Coordinate, Room> layout = new HashMap<>();
        Coordinate spawnCoord = new Coordinate(0, 0);
        Room dummyRoom = new DummyRoom(true);
        layout.put(spawnCoord, dummyRoom);
        Floor floor = new Floor(1, layout);
        Optional<Room> retrievedRoom = floor.getRoomAt(new Coordinate(0, 0));

        assertTrue(retrievedRoom.isPresent(), "La stanza dovrebbe essere presente alle coordinate (0,0)");
        assertEquals(dummyRoom, retrievedRoom.get(), "La stanza estratta deve essere esattamente quella inserita nel layout");
    }

    @Test
    void testGetRoomAtReturnsEmptyOptionalForInvalidCoordinate() {
        Map<Coordinate, Room> layout = new HashMap<>();
        Floor floor = new Floor(1, layout);
        Optional<Room> emptyResult = floor.getRoomAt(new Coordinate(99, 99));

        assertTrue(emptyResult.isEmpty(), "Richiedere coordinate vuote deve restituire un Optional.empty()");
    }

    @Test
    void testFloorIsClearedWhenAllRoomsAreCleared() {
        Map<Coordinate, Room> layout = new HashMap<>();
        layout.put(new Coordinate(0, 0), new DummyRoom(true));
        layout.put(new Coordinate(1, 0), new DummyRoom(true));
        Floor floor = new Floor(1, layout);
        assertTrue(floor.isCleared(), "Il piano deve risultare completato se tutte le stanze lo sono");
    }

    @Test
    void testFloorIsNotClearedWhenAtLeastOneRoomHasEnemies() {
        Map<Coordinate, Room> layout = new HashMap<>();
        layout.put(new Coordinate(0, 0), new DummyRoom(true));
        layout.put(new Coordinate(1, 0), new DummyRoom(false));
        Floor floor = new Floor(1, layout);


        assertFalse(floor.isCleared(), "Il piano NON deve risultare completato se c'è almeno una stanza con nemici vivi");
    }

    private static class DummyRoom implements Room {
        private  boolean isCleared;

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
    }
}