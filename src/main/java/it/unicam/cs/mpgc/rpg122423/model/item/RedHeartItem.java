package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class RedHeartItem extends Item {
    public RedHeartItem() {
        super("Cuore", "/assets/items/hp/Red_Heart.png");
    }

    @Override
    public void onPickup(Player player) {
        player.heal(2);
    }
}
