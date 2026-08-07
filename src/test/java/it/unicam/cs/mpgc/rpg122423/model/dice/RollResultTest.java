package it.unicam.cs.mpgc.rpg122423.model.dice;

import it.unicam.cs.mpgc.rpg122423.dto.RollResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class RollResultTest {

    @Test
    void testImmutabilityFromExternalChanges() {
        List<Integer> originalList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        RollResult result = new RollResult(originalList);
        originalList.add(6);
        assertEquals(5, result.values().size(),"Il RollResult non deve subire le modifiche della lista originale usata per crearlo.");
    }

    @Test
    void testImmutabilityDirectModification() {
        List<Integer> originalList = List.of(1, 2, 3, 4, 5);
        RollResult result = new RollResult(originalList);
        assertThrows(UnsupportedOperationException.class, () -> {
            result.values().add(6);
        }, "Tentare di modificare i valori del record deve lanciare un'eccezione.");
    }
}
