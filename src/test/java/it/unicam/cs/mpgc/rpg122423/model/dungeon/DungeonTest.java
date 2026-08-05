package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DungeonTest {

    @Test
    void testDungeonInitialization() {
        Dungeon dungeon = new Dungeon();

        assertEquals(1, dungeon.getCurrentFloorNumber(), "Il dungeon deve iniziare dal piano 1.");
        assertNotNull(dungeon.getCurrentFloor(), "Il piano corrente deve essere inizializzato.");
        assertEquals(1, dungeon.getCurrentFloor().getFloorNumber(), "Il numero del Floor deve coincidere con quello del Dungeon.");
        assertFalse(dungeon.getCurrentFloor().isCleared(), "Il piano appena generato non deve essere completato.");
    }

    @Test
    void testAdvanceToNextFloorFailsIfNotCleared() {
        Dungeon dungeon = new Dungeon();
        Floor initialFloor = dungeon.getCurrentFloor();

        // Tentativo di discesa bloccato silenziomente
        assertFalse(dungeon.advanceToNextFloor(), "Il metodo deve restituire false se il piano non è completato.");
        assertEquals(1, dungeon.getCurrentFloorNumber(), "Il numero del piano non deve incrementare.");
        assertSame(initialFloor, dungeon.getCurrentFloor(), "Il piano corrente non deve essere sovrascritto.");
    }

    @Test
    void testAdvanceToNextFloorSucceedsIfCleared() {
        Dungeon dungeon = new Dungeon();
        Floor initialFloor = dungeon.getCurrentFloor();

        // Simuliamo l'uccisione del boss / completamento del livello
        initialFloor.markAsCleared();

        // Ora la discesa deve funzionare
        assertTrue(dungeon.advanceToNextFloor(), "Il metodo deve restituire true se il piano è completato.");
        assertEquals(2, dungeon.getCurrentFloorNumber(), "Il numero del piano deve incrementare a 2.");
        assertNotSame(initialFloor, dungeon.getCurrentFloor(), "Deve essere generata e caricata una nuova istanza di Floor.");
        assertEquals(2, dungeon.getCurrentFloor().getFloorNumber(), "Il nuovo Floor deve avere come numero di piano 2.");
    }
}