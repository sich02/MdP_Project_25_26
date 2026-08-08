package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.EnemyFactory;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.List;

public class CombatRoom implements Room, Lootable {

    private boolean cleared;
    private boolean lootAvailable;
    private final boolean generatesLoot;
    private final List<Enemy> enemies;

    // --- NUOVE VARIABILI PER IL COMBATTIMENTO ---
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    // Aggiungiamo il floorNumber per dire alla Factory che pool usare
    public CombatRoom(boolean generatesLoot, int floorNumber) {
        this.cleared = false;
        this.generatesLoot = generatesLoot;
        this.lootAvailable = generatesLoot;

        // La Factory ci restituisce da 1 a 5 nemici
        this.enemies = EnemyFactory.generateEnemiesForFloor(floorNumber);

        // Il primo turno spetta SEMPRE al Player
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void resolveEncounter() { this.cleared = true; }

    // --- GESTIONE DEI TURNI ---
    public TurnPhase getCurrentPhase() { return currentPhase; }
    public void setPhase(TurnPhase phase) { this.currentPhase = phase; }

    public int getCurrentEnemyTurnIndex() { return currentEnemyTurnIndex; }
    public void advanceEnemyTurnIndex() { this.currentEnemyTurnIndex++; }
    public void resetEnemyTurnIndex() { this.currentEnemyTurnIndex = 0; }

    @Override
    public boolean isCleared() {
        boolean allDead = true;
        for (Enemy e : enemies) {
            if (!e.isDead()) {
                allDead = false;
                break;
            }
        }
        if (allDead) {
            this.cleared = true;
            return true;
        }

        return false;
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