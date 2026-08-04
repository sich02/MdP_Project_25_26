package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreasureRoomTest {

    @Test
    void testUnlockedTreasureRoom() {
        TreasureRoom room = new TreasureRoom(false); // Piano 1: nessuna chiave

        assertFalse(room.isLocked(), "La TreasureRoom del primo piano non deve essere chiusa a chiave.");
        assertFalse(room.isCleared(), "La stanza non deve essere risolta appena creata.");
        assertTrue(room.hasLoot(), "Deve avere loot disponibile di default.");
    }

    @Test
    void testLockedTreasureRoom() {
        TreasureRoom room = new TreasureRoom(true); // Dal piano 2 in poi

        assertTrue(room.isLocked(), "La TreasureRoom dal piano 2 in poi deve essere chiusa a chiave.");

        room.unlock();
        assertFalse(room.isLocked(), "Dopo unlock(), la stanza deve risultare aperta.");
    }

    @Test
    void testClaimLootClearsRoomAndIsIdempotent() {
        TreasureRoom room = new TreasureRoom(false);

        room.claimLoot();

        assertFalse(room.hasLoot(), "Dopo claimLoot(), non deve esserci più loot disponibile.");
        assertTrue(room.isCleared(), "Raccogliere il loot deve marcare la stanza come risolta.");

        // Testiamo la rimozione dell'if: chiamare di nuovo claimLoot non deve lanciare eccezioni
        assertDoesNotThrow(room::claimLoot, "Chiamare claimLoot() su una stanza già depredata non deve lanciare eccezioni.");
        assertFalse(room.hasLoot(), "Il loot deve rimanere non disponibile.");
    }
}