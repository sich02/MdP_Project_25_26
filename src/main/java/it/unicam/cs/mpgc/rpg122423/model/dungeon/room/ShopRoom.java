package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Stanza del negozio. Gli oggetti in vendita vengono forniti dall'esterno.
 */
public class ShopRoom implements Room, Lockable {
    private boolean isLocked;

    /**
     * Rappresenta un oggetto acquistabile nel negozio.
     * Incapsula lo stato di acquisto.
     */
    public static class Purchasable {
        private final Item item;
        private final int price;
        private boolean bought;

        public Purchasable(Item item, int price) {
            this.item = item;
            this.price = price;
            this.bought = false;
        }

        public Item getItem() { return item; }
        public int getPrice() { return price; }
        public boolean isBought() { return bought; }
        public void markAsBought() { this.bought = true; }
    }

    private final List<Purchasable> itemsForSale;

    /**
     * Crea una ShopRoom con gli oggetti in vendita forniti dall'esterno.
     *
     * @param requiresKey se la stanza richiede una chiave per entrare
     * @param itemsForSale la lista di oggetti acquistabili, generata dall'esterno
     */
    public ShopRoom(boolean requiresKey, List<Purchasable> itemsForSale) {
        this.isLocked = requiresKey;
        this.itemsForSale = new ArrayList<>(itemsForSale);
    }

    public List<Purchasable> getItemsForSale() {
        return itemsForSale;
    }

    @Override
    public boolean isCleared() { return true; }

    @Override
    public void markAsCleared() { /* ShopRoom è sempre "cleared" per definizione. */ }

    @Override
    public boolean isLocked() { return isLocked; }

    @Override
    public void unlock() { this.isLocked = false; }

    @Override
    public RoomType getRoomType() { return RoomType.SHOP; }
}