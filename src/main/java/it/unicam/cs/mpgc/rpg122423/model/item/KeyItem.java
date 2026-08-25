package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class KeyItem extends Item {
    public KeyItem() {
        super("Chiave", "/assets/key.png");
    }

    @Override
    public void onPickup(Player player) {
        player.addKeys(1);
    }
}
