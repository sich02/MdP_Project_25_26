package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

public class CoinItem extends Item {
    public CoinItem() {
        super("Moneta", "/assets/coin.png");
    }

    @Override
    public void onPickup(Player player) {
        player.addGold(1);
    }
}
