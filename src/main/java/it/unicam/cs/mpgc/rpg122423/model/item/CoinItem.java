package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class CoinItem implements Item {
    private final String name = "Moneta";
    private final String imagePath = "/assets/coin.png";

    @Override
    public String getName() { return name; }

    @Override
    public String getImagePath() { return imagePath; }

    @Override
    public void onPickup(Player player) {
        player.addGold(1);
    }
}
