package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpawnRoomTest {

    @Test
    void testSpawnRoomIsAlwaysCleared() {
        SpawnRoom room = new SpawnRoom();

        assertTrue(room.isCleared(), "La SpawnRoom deve essere considerata risolta fin dalla sua creazione.");

        // Verifichiamo che chiamare markAsCleared non alteri lo stato
        room.markAsCleared();
        assertTrue(room.isCleared(), "Anche dopo markAsCleared, la SpawnRoom deve rimanere risolta.");
    }
}