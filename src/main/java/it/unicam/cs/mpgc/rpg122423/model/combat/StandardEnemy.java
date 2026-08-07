package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class StandardEnemy implements Enemy {
    private final String name;
    private final int maxHp;
    private int currentHp;
    private final EnemyBehavior behavior;
    private final List<StatusEffect> activeEffects;
    private EnemyAction nextAction;

    public StandardEnemy(String name, int maxHp, EnemyBehavior behavior) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.behavior = behavior;
        this.activeEffects = new ArrayList<>();
        this.prepareNextAction();
    }

    @Override
    public String getName() {return name;}

    @Override
    public int getCurrentHp() {return currentHp;}

    @Override
    public int getMaxHp() {return maxHp;}

    @Override
    public void takeDamage(int damage) {this.currentHp = Math.max(0, this.currentHp - damage);}

    @Override
    public boolean isDead() {return currentHp <= 0;}

    @Override
    public EnemyAction getNextAction() {return nextAction;}

    @Override
    public void prepareNextAction() {this.nextAction = behavior.decideAction(this);}

    @Override
    public void addStatusEffect(StatusEffect effect) {activeEffects.add(effect);}

    @Override
    public List<StatusEffect> getActiveEffects() {return List.copyOf(activeEffects);}

    @Override
    public void removeStatusEffect(StatusEffect effect) {activeEffects.remove(effect);}
}