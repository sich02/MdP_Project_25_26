package it.unicam.cs.mpgc.rpg122423.dto;

import it.unicam.cs.mpgc.rpg122423.model.dice.ComboType;
import java.util.List;

public record Combo(ComboType type, List<Integer> involvedValues, int totalDamage) {
    public Combo {
        involvedValues = List.copyOf(involvedValues);
    }
}