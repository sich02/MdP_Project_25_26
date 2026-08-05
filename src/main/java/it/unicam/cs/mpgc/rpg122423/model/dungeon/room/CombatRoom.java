package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class CombatRoom implements Room, Lootable {

    private boolean cleared;
    private boolean lootAvailable;

    public CombatRoom(boolean startsCleared) {
        this.cleared = startsCleared;
        this.lootAvailable = true;
    }

    public void resolveEncounter() {this.cleared = true;}

    @Override
    public boolean isCleared() {return cleared;}

    @Override
    public void markAsCleared() {this.cleared = true;}

    @Override
    public boolean hasLoot() {return cleared && lootAvailable;}

    @Override
    public void claimLoot() {this.lootAvailable = false;}
}