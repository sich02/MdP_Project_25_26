package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class CombatRoom implements Room, Lootable {

    private boolean cleared;
    private boolean lootAvailable;
    private final boolean generatesLoot;

    public CombatRoom(boolean generatesLoot) {
        this.cleared = false;
        this.generatesLoot = generatesLoot;
        this.lootAvailable = generatesLoot;
    }

    public void resolveEncounter() { this.cleared = true; }

    @Override
    public boolean isCleared() { return cleared; }

    @Override
    public void markAsCleared() { this.cleared = true; }

    @Override
    public boolean hasLoot() {
        return cleared && generatesLoot && lootAvailable;
    }

    @Override
    public void claimLoot() { this.lootAvailable = false; }
}