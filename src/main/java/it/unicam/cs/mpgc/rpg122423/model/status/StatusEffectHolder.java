package it.unicam.cs.mpgc.rpg122423.model.status;

import java.util.List;

public interface StatusEffectHolder {
    void addStatusEffect(StatusEffect effect);
    List<StatusEffect> getActiveEffects();
    void removeStatusEffect(StatusEffect effect);
}