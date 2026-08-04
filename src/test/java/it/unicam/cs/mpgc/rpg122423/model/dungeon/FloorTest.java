package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FloorTest {

    @Test
    void testValidFloorCreation() {
        Floor floor = new Floor(1);

        assertEquals(1, floor.getFloorNumber(), "Il numero del piano deve corrispondere a quello passato nel costruttore.");
        assertFalse(floor.isCleared(), "Un piano appena creato non deve essere considerato completato.");
    }

    @Test
    void testInvalidFloorCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Floor(0),
                "Creare un piano con numero 0 deve lanciare un'eccezione.");

        assertThrows(IllegalArgumentException.class, () -> new Floor(-5),
                "Creare un piano con numero negativo deve lanciare un'eccezione.");
    }

    @Test
    void testMarkAsCleared() {
        Floor floor = new Floor(2);

        floor.markAsCleared();

        assertTrue(floor.isCleared(), "Dopo aver richiamato markAsCleared(), il piano deve risultare completato.");
    }
}