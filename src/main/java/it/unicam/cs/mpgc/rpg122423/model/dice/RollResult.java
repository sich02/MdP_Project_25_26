package it.unicam.cs.mpgc.rpg122423.model.dice;

import java.util.List;

public record RollResult(List<Integer> values) {
    public RollResult {values = List.copyOf(values);}
}
