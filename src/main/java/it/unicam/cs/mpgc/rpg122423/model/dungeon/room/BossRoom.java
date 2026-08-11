package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.BossFactory;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.List;

/**
 * Stanza del Boss. Contiene un singolo boss con stats scalate al piano.
 * Dopo la sconfitta del boss, appare una botola per avanzare al piano successivo.
 */
public class BossRoom implements Room {

    private boolean cleared = false;
    private boolean trapdoorActive = false;
    private final Enemy boss;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    public BossRoom(int floorNumber) {
        this.boss = BossFactory.createBossForFloor(floorNumber);
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
    }

    public List<Enemy> getEnemies() {
        return List.of(boss);
    }

    public Enemy getBoss() {
        return boss;
    }

    public boolean isTrapdoorActive() {
        return trapdoorActive;
    }

    // --- Gestione dei turni (come CombatRoom) ---
    public TurnPhase getCurrentPhase() { return currentPhase; }
    public void setPhase(TurnPhase phase) { this.currentPhase = phase; }

    public int getCurrentEnemyTurnIndex() { return currentEnemyTurnIndex; }
    public void advanceEnemyTurnIndex() { this.currentEnemyTurnIndex++; }
    public void resetEnemyTurnIndex() { this.currentEnemyTurnIndex = 0; }

    @Override
    public boolean isCleared() {
        if (boss.isDead()) {
            this.cleared = true;
            this.trapdoorActive = true;
        }
        return cleared;
    }

    @Override
    public void markAsCleared() {
        this.cleared = true;
        this.trapdoorActive = true;
    }
}
