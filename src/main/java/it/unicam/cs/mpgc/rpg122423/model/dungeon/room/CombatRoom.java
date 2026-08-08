package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy;

public class CombatRoom implements Room, Lootable {

    private boolean cleared;
    private boolean lootAvailable;
    private final boolean generatesLoot;
    private Enemy enemy;

    public CombatRoom(boolean generatesLoot) {
        this.cleared = false;
        this.generatesLoot = generatesLoot;
        this.lootAvailable = generatesLoot;

        this.enemy = new StandardEnemy("Slime", 15, 2);
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public void resolveEncounter() { this.cleared = true; }

    @Override
    public boolean isCleared() {
        if (enemy != null && !enemy.isDead()) {
            return false;
        }
        return cleared;
    }

    @Override
    public void markAsCleared() { this.cleared = true; }

    @Override
    public boolean hasLoot() {
        return isCleared() && generatesLoot && lootAvailable;
    }

    @Override
    public void claimLoot() { this.lootAvailable = false; }
}