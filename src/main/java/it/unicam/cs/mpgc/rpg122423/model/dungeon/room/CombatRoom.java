package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.List;

public class CombatRoom implements Room, Lootable, Combattable {

    private boolean cleared;
    private boolean lootAvailable;
    private final boolean generatesLoot;
    private final List<Enemy> enemies;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    /**
     * Crea una stanza di combattimento con i nemici forniti dall'esterno.
     *
     * @param generatesLoot se la stanza genera loot alla fine del combattimento
     * @param enemies       lista di nemici già generati dal service
     */
    public CombatRoom(boolean generatesLoot, List<Enemy> enemies) {
        this.cleared = false;
        this.generatesLoot = generatesLoot;
        this.lootAvailable = generatesLoot;
        this.enemies = enemies;
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
    }

    // --- Combattable ---
    @Override public List<Enemy> getEnemies() { return enemies; }
    @Override public TurnPhase getCurrentPhase() { return currentPhase; }
    @Override public void setPhase(TurnPhase phase) { this.currentPhase = phase; }
    @Override public int getCurrentEnemyTurnIndex() { return currentEnemyTurnIndex; }
    @Override public void advanceEnemyTurnIndex() { this.currentEnemyTurnIndex++; }
    @Override public void resetEnemyTurnIndex() { this.currentEnemyTurnIndex = 0; }

    // --- Room ---
    @Override
    public boolean isCleared() {
        if (cleared) return true;

        boolean allDead = true;
        for (Enemy e : enemies) {
            if (!e.isDead()) {
                allDead = false;
                break;
            }
        }
        if (allDead) {
            this.cleared = true;
        }
        return cleared;
    }

    @Override
    public void markAsCleared() { this.cleared = true; }

    @Override
    public String getRoomType() { return "NORMAL"; }

    // --- Lootable ---
    @Override
    public boolean hasLoot() { return isCleared() && generatesLoot && lootAvailable; }

    @Override
    public void claimLoot() { this.lootAvailable = false; }
}