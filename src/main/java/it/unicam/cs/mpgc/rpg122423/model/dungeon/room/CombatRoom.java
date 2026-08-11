package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombatRoom implements Room, Lootable, Combattable {

    private static final Random RANDOM = new Random();

    private boolean cleared;
    private boolean lootAvailable;
    private final boolean generatesLoot;
    private final List<Enemy> enemies;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    public CombatRoom(boolean generatesLoot, int floorNumber) {
        this.cleared = false;
        this.generatesLoot = generatesLoot;
        this.lootAvailable = generatesLoot;
        this.enemies = generateEnemies(floorNumber);
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
    }

    /**
     * Genera da 1 a 5 nemici dalla pool del piano.
     * La logica è inlined qui (nessun Factory pattern).
     */
    private static List<Enemy> generateEnemies(int floorNumber) {
        int enemyCount = RANDOM.nextInt(5) + 1;
        List<Enemy> result = new ArrayList<>();
        for (int i = 0; i < enemyCount; i++) {
            result.add(pickRandomEnemy(floorNumber));
        }
        return result;
    }

    private static Enemy pickRandomEnemy(int floorNumber) {
        if (floorNumber == 1) {
            int roll = RANDOM.nextInt(3);
            return switch (roll) {
                case 0 -> new StandardEnemy("Black Bony", 15, 2);
                case 1 -> new StandardEnemy("Black Globin", 20, 3);
                case 2 -> new StandardEnemy("Black Knight", 25, 4);
                default -> new StandardEnemy("Cultist", 10, 2);
            };
        } else {
            int roll = RANDOM.nextInt(2);
            return switch (roll) {
                case 0 -> new StandardEnemy("Orc", 30, 3);
                default -> new StandardEnemy("Goblin", 20, 2);
            };
        }
    }

    public void resolveEncounter() { this.cleared = true; }

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

    // --- Lootable ---
    @Override
    public boolean hasLoot() { return isCleared() && generatesLoot && lootAvailable; }

    @Override
    public void claimLoot() { this.lootAvailable = false; }
}