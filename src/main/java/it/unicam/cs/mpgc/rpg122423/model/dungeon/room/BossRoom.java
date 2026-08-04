package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class BossRoom implements Room {
    private boolean cleared = false;
    @Override public boolean isCleared() { return cleared; }
    @Override public void markAsCleared() { this.cleared = true; }
}
