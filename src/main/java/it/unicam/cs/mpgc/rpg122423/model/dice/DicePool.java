package it.unicam.cs.mpgc.rpg122423.model.dice;

import java.util.ArrayList;
import java.util.List;

public class DicePool {
    private static final int DEFAULT_POOL_SIZE = 5;
    private final List<Dice> diceList;

    public DicePool() {
        this.diceList = new ArrayList<>(DEFAULT_POOL_SIZE);
        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            this.diceList.add(new Dice());
        }
    }

    public void rollAll() {
        for (Dice dice : diceList) {
            dice.roll();
        }
    }

    public void addDice() {
        this.diceList.add(new Dice());
    }

    public List<Integer> getValues() {
        List<Integer> values = new ArrayList<>(diceList.size());
        for (Dice dice : diceList) {
            values.add(dice.getCurrentValue());
        }
        return List.copyOf(values);
    }

    public void rollSpecific(List<Integer> indices) {
        for (Integer index : indices) {
            if (index >= 0 && index < diceList.size()) {
                diceList.get(index).roll();
            }
        }
    }

    public int getSize() {
        return diceList.size();
    }

    /**
     * Restituisce una vista non modificabile della lista dei dadi.
     * I singoli dadi restano mutabili (roll, setElement), ma non è possibile
     * aggiungere o rimuovere dadi dall'esterno.
     */
    public List<Dice> getDiceList() {
        return java.util.Collections.unmodifiableList(diceList);
    }
}
