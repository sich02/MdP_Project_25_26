package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

/**
 * Oggetto che incrementa il danno bonus fisso del giocatore.
 */
public class DamageItem implements Item {
    private final String name;
    private final String imagePath;
    private final int bonusDamage;

    public DamageItem(String name, String imagePath, int bonusDamage) {
        this.name = name;
        this.imagePath = imagePath;
        this.bonusDamage = bonusDamage;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getImagePath() { return imagePath; }

    @Override
    public void onPickup(Player player) {
        player.addBonusDamage(bonusDamage);
    }
}
