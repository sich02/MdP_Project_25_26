package it.unicam.cs.mpgc.rpg122423.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DiceTest {

    @Test
    public void testDiceFace() {
        Dice dice = new Dice();
        assertEquals(6, dice.getFaces(), "Il dado deve avere esattamente 6 facce.");
    }

    @Test
    void testInitiationRoll() {
        Dice dice = new Dice();
        int initialValue = dice.getCurrentValue();
        assertTrue(initialValue >= 1 && initialValue <= 6, "Al momento della creazione, il valore iniziale deve essere compreso tra 1 e 6.");
    }

    @Test
    void testRollRange() {
        Dice dice = new Dice();
        for (int i = 0; i < 1000; i++) {
            dice.roll();
            int value = dice.getCurrentValue();
            assertTrue(value >= 1 && value <= 6, "Valore dopo il lancio:"+value);
        }
    }
}
