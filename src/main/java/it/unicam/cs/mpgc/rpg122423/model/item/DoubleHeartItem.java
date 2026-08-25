package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class DoubleHeartItem extends Item {
    public DoubleHeartItem() {
        super("Cuore Doppio", "/assets/items/hp/Double_Heart.png");
    }

    @Override
    public void onPickup(Player player) {
        player.heal(4);
    }
}
