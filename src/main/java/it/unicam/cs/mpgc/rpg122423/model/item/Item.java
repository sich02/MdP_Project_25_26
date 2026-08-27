package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

/**
 * Rappresenta un oggetto collezionabile nel gioco.
 */
public interface Item {
    String getName();
    String getImagePath();

    /**
     * Applica l'effetto dell'oggetto sul giocatore.
     * @param player il giocatore
     */
    void onPickup(Player player);
}
