package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;
import it.unicam.cs.mpgc.rpg122423.model.item.Item;

import java.util.List;

/**
 * Stanza del Boss. Contiene un singolo boss fornito dall'esterno.
 * Dopo la sconfitta del boss, appare una botola per avanzare al piano successivo e un oggetto.
 */
public class BossRoom implements Room, Combattable, Lootable {

    private boolean cleared = false;
    private boolean trapdoorActive = false;
    private boolean lootAvailable = false;
    private final Item lootItem;
    private final Enemy boss;
    private TurnPhase currentPhase;
    private int currentEnemyTurnIndex;

    /**
     * Crea una stanza del Boss con il boss e il loot forniti dall'esterno (DIP).
     *
     * @param boss     il nemico boss già creato dal service
     * @param lootItem l'oggetto di loot generato dall'esterno
     */
    public BossRoom(Enemy boss, Item lootItem) {
        this.boss = boss;
        this.lootItem = lootItem;
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

    /**
     * Query pura: verifica se il boss è morto. Se sì, attiva la botola e il loot (LSP).
     */
    @Override
    public boolean isCleared() {
        if (boss.isDead() && !this.cleared) {
            markAsCleared();
        }
        return cleared;
    }

    @Override
    public void markAsCleared() {
        if (!this.cleared) {
            this.cleared = true;
            this.trapdoorActive = true;
            this.lootAvailable = true;
        }
    }

    @Override
    public String getRoomType() { return "BOSS"; }

    // --- Lootable ---
    @Override
    public boolean hasLoot() { return lootAvailable; }

    @Override
    public Item getLoot() { return lootItem; }

    @Override
    public void claimLoot() { this.lootAvailable = false; }
}
