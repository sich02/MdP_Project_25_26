package it.unicam.cs.mpgc.rpg122423.model.dice;

import it.unicam.cs.mpgc.rpg122423.dto.RollResult;

import java.util.ArrayList;
import java.util.List;

public class DicePool {
    private static final int DEFAULT_POOL_SIZE = 5;
    private final List<Dice> diceList;

    public DicePool() {
        this.diceList = new ArrayList<>(DEFAULT_POOL_SIZE);
        for(int i=0; i<DEFAULT_POOL_SIZE; i++) {
            this.diceList.add(new Dice());
        }
    }

    public void rollAll(){
        for (Dice dice : diceList) {
            dice.roll();
        }
    }

    public void addDice(){
        this.diceList.add(new Dice());
    }

    public RollResult getResult(){
        List<Integer> values = new ArrayList<>(diceList.size());
        for(Dice dice : diceList){
            values.add(dice.getCurrentValue());
        }
        return new RollResult(values);
    }

    public void rollSpecific(List<Integer> indices) {
        for (Integer index : indices) {
            if (index >= 0 && index < diceList.size()) {
                diceList.get(index).roll();
            }
        }
    }

    public int getSize(){
        return diceList.size();
    }
}
