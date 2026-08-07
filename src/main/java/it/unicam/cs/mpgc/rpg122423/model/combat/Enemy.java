package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffectHolder;

public interface Enemy extends StatusEffectHolder {
    String getName();
    int getCurrentHp();
    int getMaxHp();
    void takeDamage(int damage);
    boolean isDead();
    EnemyAction getNextAction();
    void prepareNextAction();
}