package it.unicam.cs.mpgc.rpg122423.dto;

import java.util.List;

public record RollResult(List<Integer> values) {
    public RollResult {values = List.copyOf(values);}
}
