package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;
import it.unicam.cs.mpgc.rpg122423.model.item.Item;

import java.util.List;

public class CombatRoom implements Room, Lootable, Combattable {

    private boolean cleared;
    private boolean lootAvailable;
    private final boolean generatesLoot;
    private final List<Enemy> enemies;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;
    private final Item lootItem;

    /**
     * Crea una stanza di combattimento con i nemici forniti dall'esterno.
     *
     * @param generatesLoot se la stanza genera loot alla fine del combattimento
     * @param enemies       lista di nemici già generati dal service
     * @param lootItem      l'oggetto di loot generato
     */
    public CombatRoom(boolean generatesLoot, List<Enemy> enemies, Item lootItem) {
        this.cleared = false;
        this.generatesLoot = generatesLoot;
        this.lootAvailable = generatesLoot;
        this.enemies = enemies;
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.currentEnemyTurnIndex = 0;
        this.lootItem = lootItem;
    }

    // --- Combattable ---
    @Override public List<Enemy> getEnemies() { return enemies; }
    @Override public TurnPhase getCurrentPhase() { return currentPhase; }
    @Override public void setPhase(TurnPhase phase) { this.currentPhase = phase; }
    @Override public int getCurrentEnemyTurnIndex() { return currentEnemyTurnIndex; }
    @Override public void advanceEnemyTurnIndex() { this.currentEnemyTurnIndex++; }
    @Override public void resetEnemyTurnIndex() { this.currentEnemyTurnIndex = 0; }

    // --- Room --- (Fix LSP: isCleared() è una pura query senza side-effect)
    @Override
    public boolean isCleared() {
        return cleared;
    }

    @Override
    public void markAsCleared() { this.cleared = true; }

    /**
     * Verifica se tutti i nemici sono morti e, in tal caso, marca la stanza come completata.
     * Questo metodo è un comando esplicito, separato dalla query isCleared().
     */
    public void checkAndClearIfAllDead() {
        if (!cleared) {
            boolean allDead = enemies.stream().allMatch(Enemy::isDead);
            if (allDead) {
                this.cleared = true;
            }
        }
    }

    @Override
    public RoomType getRoomType() { return RoomType.NORMAL; }

    // --- Lootable ---
    @Override
    public boolean hasLoot() { return isCleared() && generatesLoot && lootAvailable && lootItem != null; }

    @Override
    public Item getLoot() { return lootItem; }

    @Override
    public void claimLoot() { this.lootAvailable = false; }
}