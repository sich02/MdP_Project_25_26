package it.unicam.cs.mpgc.rpg122423.model.status;

public interface StatusEffect {
    String getName();
    int getRemainingTurns();
    void tick();
    boolean isExpired();
}