package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class CombatRoom implements Room, Lootable {
    private boolean cleared;
    private boolean lootAvailable;

    public CombatRoom(boolean generatesLoot) {
        this.cleared = false;
        this.lootAvailable = generatesLoot;
    }

    @Override
    public boolean isCleared() {return cleared;}

    @Override
    public void markAsCleared() {this.cleared = true;}

    @Override
    public boolean hasLoot() {return cleared&&lootAvailable;}

    @Override
    public void claimLoot() {
        this.lootAvailable = false;
    }
}
