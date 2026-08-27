package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class KeyItem implements Item {
    private final String name = "Chiave";
    private final String imagePath = "/assets/key.png";

    @Override
    public String getName() { return name; }

    @Override
    public String getImagePath() { return imagePath; }

    @Override
    public void onPickup(Player player) {
        player.addKeys(1);
    }
}
