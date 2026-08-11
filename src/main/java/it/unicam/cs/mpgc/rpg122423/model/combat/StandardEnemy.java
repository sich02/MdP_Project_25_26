package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;

/**
 * Nemico standard del dungeon. Attacca sempre con il danno base.
 */
public class StandardEnemy extends AbstractEnemy {

    public StandardEnemy(String name, int maxHp, int baseDamage) {
        super(name, maxHp, baseDamage);
    }

    @Override
    public void prepareNextAction() {
        setNextAction(new EnemyAction(getName() + " attacca!", baseDamage, null));
    }
}