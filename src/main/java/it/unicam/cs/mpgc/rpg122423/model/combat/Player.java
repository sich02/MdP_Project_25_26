package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.model.dice.DicePool;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffectHolder;

import java.util.ArrayList;
import java.util.List;

public class Player implements StatusEffectHolder {
    private final int maxHp;
    private int currentHp;
    private int gold;
    private int keys;
    private final DicePool dicePool;
    private final List<StatusEffect> activeEffects;

    public Player(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.gold = 0;
        this.keys = 0;
        this.dicePool = new DicePool();
        this.activeEffects = new ArrayList<>();
    }

    public void takeDamage(int damage) {this.currentHp = Math.max(0, this.currentHp - damage);}
    public boolean isDead() {return currentHp <= 0;}

    public void consumeKey() {
        if (keys > 0) {
            keys--;
        }
    }


    public void heal(int amount) {this.currentHp = Math.min(maxHp, this.currentHp + amount);}

    public void addGold(int amount) {this.gold += amount;}

    public void spendGold(int amount) {this.gold = Math.max(0, this.gold - amount);}

    public void addKeys(int amount) {this.keys += amount;}

    public DicePool getDicePool() {return dicePool;}

    @Override
    public void addStatusEffect(StatusEffect effect) {activeEffects.add(effect);}

    @Override
    public List<StatusEffect> getActiveEffects() {return List.copyOf(activeEffects);}

    @Override
    public void removeStatusEffect(StatusEffect effect) {activeEffects.remove(effect);}

    public int getCurrentHp() {return currentHp;}
    public int getMaxHp() {return maxHp;}
    public int getGold() {return gold;}
    public int getKeys() {return keys;}
}