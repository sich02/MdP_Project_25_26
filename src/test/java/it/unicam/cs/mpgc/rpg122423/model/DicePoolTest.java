package it.unicam.cs.mpgc.rpg122423.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DicePoolTest {

    @Test
    void testInitialPoolSize() {
        DicePool pool = new DicePool();
        assertEquals(5, pool.getSize(), "Il DicePool deve inizializzarsi sempre con 5 dadi.");
        RollResult result = pool.getResult();
        assertEquals(5, result.values().size(), "Lo snapshot iniziale deve contenere 5 risultati.");
    }

    @Test
    void testAddDice() {
        DicePool pool = new DicePool();
        pool.addDice();
        assertEquals(6, pool.getSize(), "L'aggiunta di un dado deve portare la dimensione a 6.");
        assertEquals(6, pool.getResult().values().size());
    }

    @Test
    void testRollAllGeneratesValidValues() {
        DicePool pool = new DicePool();
        pool.rollAll();
        RollResult result = pool.getResult();
        for (int value : result.values()) {
            assertTrue(value >= 1 && value <= 6,"Ogni dado nel pool deve generare un valore valido tra 1 e 6 dopo un rollAll(). Valore trovato: " + value);
        }
    }
}