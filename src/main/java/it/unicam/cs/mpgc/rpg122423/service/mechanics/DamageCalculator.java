package it.unicam.cs.mpgc.rpg122423.service.mechanics;

import it.unicam.cs.mpgc.rpg122423.dto.Combo;

public class DamageCalculator {

    public int calculateComboDamage(Combo combo) {
        return combo.totalDamage();
    }
}