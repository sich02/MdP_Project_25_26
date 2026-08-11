package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;

/**
 * Boss nemico con stats che scalano in base al piano.
 * Alterna attacco normale e attacco pesante (danno doppio).
 */
public class BossEnemy extends AbstractEnemy {

    private boolean nextIsHeavy = false;

    /**
     * Crea un boss con stats scalate in base al piano.
     *
     * @param name        nome del boss
     * @param baseHp      HP base (piano 1)
     * @param baseDamage  danno base (piano 1)
     * @param floorNumber numero del piano corrente
     */
    public BossEnemy(String name, int baseHp, int baseDamage, int floorNumber) {
        super(name, baseHp + (floorNumber - 1) * 10, baseDamage + (floorNumber - 1) * 2);
    }

    @Override
    public void prepareNextAction() {
        if (nextIsHeavy) {
            int heavyDmg = baseDamage * 2;
            setNextAction(new EnemyAction(getName() + " carica un attacco devastante!", heavyDmg, null));
        } else {
            setNextAction(new EnemyAction(getName() + " attacca!", baseDamage, null));
        }
        nextIsHeavy = !nextIsHeavy;
    }
}
