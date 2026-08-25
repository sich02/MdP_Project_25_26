package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class HalfHeartItem extends Item {
    public HalfHeartItem() {
        super("Mezzo Cuore", "/assets/items/hp/Half_Red_Heart.png");
    }

    @Override
    public void onPickup(Player player) {
        player.heal(1);
    }
}
