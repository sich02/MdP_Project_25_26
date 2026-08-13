package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

/**
 * Oggetto che incrementa il danno bonus fisso del giocatore.
 */
public class DamageItem extends Item {
    private final int bonusDamage;

    public DamageItem(String name, String imagePath, int bonusDamage) {
        super(name, imagePath);
        this.bonusDamage = bonusDamage;
    }

    @Override
    public void onPickup(Player player) {
        player.addBonusDamage(bonusDamage);
    }
}
