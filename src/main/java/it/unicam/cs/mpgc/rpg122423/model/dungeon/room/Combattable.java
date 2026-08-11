package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.List;

/**
 * Interfaccia per le stanze che ospitano combattimenti (CombatRoom, BossRoom).
 * Permette al service di operare su un'unica astrazione senza instanceof.
 */
public interface Combattable {
    List<Enemy> getEnemies();
    TurnPhase getCurrentPhase();
    void setPhase(TurnPhase phase);
    int getCurrentEnemyTurnIndex();
    void advanceEnemyTurnIndex();
    void resetEnemyTurnIndex();
}
