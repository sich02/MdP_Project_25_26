package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class RedHeartItem implements Item {
    private final String name = "Cuore";
    private final String imagePath = "/assets/items/hp/Red_Heart.png";

    @Override
    public String getName() { return name; }

    @Override
    public String getImagePath() { return imagePath; }

    @Override
    public void onPickup(Player player) {
        player.heal(2);
    }
}
