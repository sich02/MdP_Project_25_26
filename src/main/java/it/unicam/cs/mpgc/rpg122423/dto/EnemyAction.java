package it.unicam.cs.mpgc.rpg122423.dto;

import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;

public record EnemyAction(String description, int damage, StatusEffect statusEffect) {
    public EnemyAction {
        if (damage < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo.");
        }
    }
}