package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

/**
 * Oggetto che incrementa la vita massima e cura il giocatore.
 */
public class HpItem extends Item {

    public HpItem(String name, String imagePath) {
        super(name, imagePath);
    }

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
