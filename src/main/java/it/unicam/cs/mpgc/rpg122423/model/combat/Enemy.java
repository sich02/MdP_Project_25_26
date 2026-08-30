package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffectHolder;

public interface Enemy extends StatusEffectHolder {
    String getName();
    int getCurrentHp();
    int getMaxHp();
    int getBaseDamage();
    String getSpritePath();
    void takeDamage(int damage);
    boolean isDead();
    EnemyAction getNextAction();
    void prepareNextAction();
    void tickStatusEffects();
}