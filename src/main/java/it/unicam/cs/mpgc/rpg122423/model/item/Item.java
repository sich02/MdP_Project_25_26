package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;

/**
 * Rappresenta un oggetto collezionabile nel gioco.
 */
public abstract class Item {
    private final String name;
    private final String imagePath;
    
    public Item(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }
    
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    
    /**
     * Applica l'effetto dell'oggetto sul giocatore.
     * @param player il giocatore
     */
    public abstract void onPickup(Player player);
}
