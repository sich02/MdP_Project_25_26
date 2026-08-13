package it.unicam.cs.mpgc.rpg122423.model.dice;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DicePoolTest {

    @Test
    void testInitialPoolSize() {
        DicePool pool = new DicePool();
        assertEquals(5, pool.getSize(), "Il DicePool deve inizializzarsi sempre con 5 dadi.");
        java.util.List<Integer> result = pool.getValues();
        assertEquals(5, result.size(), "Lo snapshot iniziale deve contenere 5 risultati.");
    }

    @Test
    void testAddDice() {
        DicePool pool = new DicePool();
        pool.addDice();
        assertEquals(6, pool.getSize(), "L'aggiunta di un dado deve portare la dimensione a 6.");
        assertEquals(6, pool.getValues().size());
    }

    @Test
    void testRollAllGeneratesValidValues() {
        DicePool pool = new DicePool();
        pool.rollAll();
        java.util.List<Integer> result = pool.getValues();
        for (int value : result) {
            assertTrue(value >= 1 && value <= 6,"Ogni dado nel pool deve generare un valore valido tra 1 e 6 dopo un rollAll(). Valore trovato: " + value);
        }
    }
}