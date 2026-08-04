package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DungeonTest {

    @Test
    void testValidDungeonCreation() {
        Dungeon dungeon = new Dungeon(10); // 10 è maggiore di 5, quindi valido

        assertEquals(10, dungeon.getTotalFloors(), "Il dungeon deve contenere esattamente il numero di piani richiesti.");
        assertEquals(1, dungeon.getCurrentFloor().getFloorNumber(), "La run deve iniziare sempre dal piano 1.");
        assertTrue(dungeon.hasNextFloor(), "Se ci sono 10 piani e siamo al primo, deve esserci un piano successivo.");
    }

    @Test
    void testInvalidDungeonCreation() {
        // Ora verifichiamo la tua nuova condizione limite (<= 5)
        assertThrows(IllegalArgumentException.class, () -> new Dungeon(5),
                "Creare un dungeon con 5 piani deve lanciare un'eccezione in base al nuovo vincolo.");

        assertThrows(IllegalArgumentException.class, () -> new Dungeon(0),
                "Creare un dungeon con 0 piani deve lanciare un'eccezione.");
    }

    @Test
    void testCannotAdvanceIfFloorNotCleared() {
        Dungeon dungeon = new Dungeon(6); // Aggiornato a 6 piani

        // Proviamo a scendere senza aver completato il piano 1
        assertThrows(IllegalStateException.class, dungeon::advanceToNextFloor,
                "Deve essere impossibile avanzare se il piano corrente non è stato completato.");
    }

    @Test
    void testSuccessfulAdvancement() {
        Dungeon dungeon = new Dungeon(6); // Aggiornato a 6 piani

        // Completiamo il piano 1
        dungeon.getCurrentFloor().markAsCleared();
        dungeon.advanceToNextFloor();

        assertEquals(2, dungeon.getCurrentFloor().getFloorNumber(), "Dopo l'avanzamento, il giocatore deve trovarsi al piano 2.");
    }

    @Test
    void testCannotAdvancePastLastFloor() {
        Dungeon dungeon = new Dungeon(6); // Aggiornato a 6 piani

        // Simuliamo il superamento dei primi 5 piani con un ciclo per arrivare all'ultimo
        for (int i = 0; i < 5; i++) {
            dungeon.getCurrentFloor().markAsCleared();
            dungeon.advanceToNextFloor();
        }

        // Ora siamo al piano 6 (l'ultimo). Puliamolo.
        dungeon.getCurrentFloor().markAsCleared();

        // Proviamo a scendere ancora
        assertFalse(dungeon.hasNextFloor(), "Al piano 6 di 6, non ci devono essere piani successivi.");
        assertThrows(IllegalStateException.class, dungeon::advanceToNextFloor,
                "Avanzare oltre l'ultimo piano deve lanciare un'eccezione, anche se il piano è completato.");
    }
}