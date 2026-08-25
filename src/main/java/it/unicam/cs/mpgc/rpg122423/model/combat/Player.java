package it.unicam.cs.mpgc.rpg122423.model.combat;

import it.unicam.cs.mpgc.rpg122423.model.dice.DicePool;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffectHolder;

import java.util.ArrayList;
import java.util.List;

public class Player implements StatusEffectHolder {
    private int maxHp; // Ora gestito a "mezzi cuori", massimale di 12 (6 cuori interi)
    private int currentHp;
    private int gold;
    private int keys;
    private int bonusDamage; // Danno bonus per ogni dado lanciato
    private final DicePool dicePool;
    private final List<StatusEffect> activeEffects;
    
    // Stato del turno di combattimento (spostato dalla View)
    private boolean hasRolled;
    private boolean hasAttacked;
    private int rerollsLeft;
    
    private final PlayableCharacter characterType;
    private final int maxRerolls;

    public Player(PlayableCharacter characterType) {
        this.characterType = characterType;
        this.maxHp = characterType.getBaseHp();
        this.currentHp = maxHp;
        this.gold = 0;
        this.keys = 0;
        this.bonusDamage = characterType.getBaseBonusDamage();
        this.maxRerolls = characterType.getRerollsPerTurn();
        this.dicePool = new DicePool();
        this.activeEffects = new ArrayList<>();
        resetTurnState();
    }
    
    public Player() {
        this(PlayableCharacter.KNIGHT); // Default per retrocompatibilità
    }

    public void resetTurnState() {
        this.hasRolled = false;
        this.hasAttacked = false;
        this.rerollsLeft = this.maxRerolls;
    }

    public void restoreState(int currentHp, int maxHp, int gold, int keys, int bonusDamage) {
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.gold = gold;
        this.keys = keys;
        this.bonusDamage = bonusDamage;
    }

    public boolean hasRolled() { return hasRolled; }
    public void setHasRolled(boolean hasRolled) { this.hasRolled = hasRolled; }
    
    public boolean hasAttacked() { return hasAttacked; }
    public void setHasAttacked(boolean hasAttacked) { this.hasAttacked = hasAttacked; }
    
    public int getRerollsLeft() { return rerollsLeft; }
    public void decrementRerolls() { if (rerollsLeft > 0) rerollsLeft--; }


    /**
     * Ignora l'entità del danno: qualsiasi colpo toglie 1 punto (mezzo cuore).
     */
    public void takeHit() {
        this.currentHp = Math.max(0, this.currentHp - 1);
    }

    public boolean isDead() {return currentHp <= 0;}

    public void increaseMaxHp(int amount) {
        this.maxHp = Math.min(12, this.maxHp + amount);
    }



    public void consumeKey() {
        if (keys > 0) {
            keys--;
        }
    }

    public void heal(int amount) {this.currentHp = Math.min(maxHp, this.currentHp + amount);}

    public void addGold(int amount) {this.gold += amount;}

    public void spendGold(int amount) {this.gold = Math.max(0, this.gold - amount);}

    public void addKeys(int amount) {this.keys += amount;}

    public int getBonusDamage() { return bonusDamage; }

    public void addBonusDamage(int amount) { this.bonusDamage += amount; }

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
    public PlayableCharacter getCharacterType() { return characterType; }
    public int getMaxRerolls() { return maxRerolls; }

    /**
     * Helper per l'interfaccia grafica: converte i punti interni in cuori visibili (es. 5 -> 2.5)
     */
    public double getHeartsForDisplay() {
        return currentHp / 2.0;
    }
}