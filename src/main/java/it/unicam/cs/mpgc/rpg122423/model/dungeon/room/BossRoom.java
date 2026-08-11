package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.BossEnemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.List;
import java.util.Random;

/**
 * Stanza del Boss. Contiene un singolo boss con stats scalate al piano.
 * Dopo la sconfitta del boss, appare una botola per avanzare al piano successivo.
 */
public class BossRoom implements Room, Combattable {

    private static final Random RANDOM = new Random();

    /** Pool globale dei boss: {nome, baseHp, baseDmg} */
    private static final String[][] BOSS_POOL = {
            {"Conquest", "40", "5"},
            {"Dark One", "50", "6"},
            {"Famine", "35", "7"},
            {"Little Horn", "45", "5"}
    };

    private boolean cleared = false;
    private boolean trapdoorActive = false;
    private final Enemy boss;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    public BossRoom(int floorNumber) {
        this.boss = pickRandomBoss(floorNumber);
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
    }

    /**
     * Sceglie un boss casuale dalla pool globale con stats scalate al piano.
     * Logica inlined (nessun Factory pattern).
     */
    private static BossEnemy pickRandomBoss(int floorNumber) {
        int index = RANDOM.nextInt(BOSS_POOL.length);
        String name = BOSS_POOL[index][0];
        int baseHp = Integer.parseInt(BOSS_POOL[index][1]);
        int baseDmg = Integer.parseInt(BOSS_POOL[index][2]);
        return new BossEnemy(name, baseHp, baseDmg, floorNumber);
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
}
