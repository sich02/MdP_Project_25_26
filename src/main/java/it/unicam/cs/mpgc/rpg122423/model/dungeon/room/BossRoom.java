package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.List;

/**
 * Stanza del Boss. Contiene un singolo boss fornito dall'esterno.
 * Dopo la sconfitta del boss, appare una botola per avanzare al piano successivo.
 */
public class BossRoom implements Room, Combattable {

    private boolean cleared = false;
    private boolean trapdoorActive = false;
    private final Enemy boss;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    /**
     * Crea una stanza del Boss con il boss fornito dall'esterno.
     *
     * @param boss il nemico boss già creato dal service
     */
    public BossRoom(Enemy boss) {
        this.boss = boss;
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
    }

    public Enemy getBoss() { return boss; }
    public boolean isTrapdoorActive() { return trapdoorActive; }

    // --- Combattable ---
    @Override public List<Enemy> getEnemies() { return List.of(boss); }
    @Override public TurnPhase getCurrentPhase() { return currentPhase; }
    @Override public void setPhase(TurnPhase phase) { this.currentPhase = phase; }
    @Override public int getCurrentEnemyTurnIndex() { return currentEnemyTurnIndex; }
    @Override public void advanceEnemyTurnIndex() { this.currentEnemyTurnIndex++; }
    @Override public void resetEnemyTurnIndex() { this.currentEnemyTurnIndex = 0; }

    // --- Room ---
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

    @Override
    public String getRoomType() { return "BOSS"; }
}

