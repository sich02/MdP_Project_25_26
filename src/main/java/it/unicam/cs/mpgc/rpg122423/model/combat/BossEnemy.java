package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Boss nemico con stats che scalano in base al piano.
 * Alterna attacco normale e attacco pesante (danno doppio).
 */
public class BossEnemy implements Enemy {

    private final String name;
    private final int maxHp;
    private int currentHp;
    private final int baseDamage;
    private final List<StatusEffect> activeEffects;
    private EnemyAction nextAction;
    private boolean nextIsHeavy = false;

    /**
     * Crea un boss con stats scalate in base al piano.
     *
     * @param name       nome del boss
     * @param baseHp     HP base (piano 1)
     * @param baseDamage danno base (piano 1)
     * @param floorNumber numero del piano corrente
     */
    public BossEnemy(String name, int baseHp, int baseDamage, int floorNumber) {
        this.name = name;
        this.maxHp = baseHp + (floorNumber - 1) * 10;
        this.currentHp = this.maxHp;
        this.baseDamage = baseDamage + (floorNumber - 1) * 2;
        this.activeEffects = new ArrayList<>();
        this.prepareNextAction();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCurrentHp() {
        return currentHp;
    }

    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void takeDamage(int damage) {
        this.currentHp = Math.max(0, this.currentHp - damage);
    }

    @Override
    public boolean isDead() {
        return currentHp <= 0;
    }

    @Override
    public EnemyAction getNextAction() {
        return nextAction;
    }

    @Override
    public void prepareNextAction() {
        if (nextIsHeavy) {
            int heavyDmg = baseDamage * 2;
            this.nextAction = new EnemyAction(name + " carica un attacco devastante!", heavyDmg, null);
        } else {
            this.nextAction = new EnemyAction(name + " attacca!", baseDamage, null);
        }
        nextIsHeavy = !nextIsHeavy;
    }

    @Override
    public void addStatusEffect(StatusEffect effect) {
        activeEffects.add(effect);
    }

    @Override
    public List<StatusEffect> getActiveEffects() {
        return List.copyOf(activeEffects);
    }

    @Override
    public void removeStatusEffect(StatusEffect effect) {
        activeEffects.remove(effect);
    }
}
