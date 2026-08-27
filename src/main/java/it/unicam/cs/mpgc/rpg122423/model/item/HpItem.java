package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

/**
 * Oggetto che incrementa la vita massima e cura il giocatore.
 */
public class HpItem implements Item {
    private final String name;
    private final String imagePath;

    public HpItem(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getImagePath() { return imagePath; }

    @Override
    public void onPickup(Player player) {
        if (player.getMaxHp() < Player.MAX_HP) {
            player.increaseMaxHp(2);
            player.heal(2);
        } else {
            player.heal(player.getMaxHp());
        }
    }
}
