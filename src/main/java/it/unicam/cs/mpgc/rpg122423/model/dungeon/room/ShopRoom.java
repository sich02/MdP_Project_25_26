package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.item.Item;
import it.unicam.cs.mpgc.rpg122423.model.item.ItemPool;
import it.unicam.cs.mpgc.rpg122423.model.item.KeyItem;
import it.unicam.cs.mpgc.rpg122423.model.item.RedHeartItem;
import it.unicam.cs.mpgc.rpg122423.model.item.HalfHeartItem;
import it.unicam.cs.mpgc.rpg122423.model.item.DoubleHeartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopRoom implements Room, Lockable {
    private boolean isLocked;

    public static class Purchasable {
        public final Item item;
        public final int price;
        public boolean isBought;

        public Purchasable(Item item, int price) {
            this.item = item;
            this.price = price;
            this.isBought = false;
        }
    }

    private final List<Purchasable> itemsForSale = new ArrayList<>();

    public ShopRoom(boolean requiresKey) {
        this.isLocked = requiresKey;
        Random random = new Random();

        // Oggetto principale: HP Item molto probabile (es. 60% + chance base), altrimenti random, a 15 monete
        Item mainItem = (random.nextInt(100) < 60) ? ItemPool.getRandomHpItem() : ItemPool.getRandomItem();
        itemsForSale.add(new Purchasable(mainItem, 15));

        // Oggetto consumabile: Chiave o Cuore a 5 monete (scontato a 3 col 25% di prob)
        int price = random.nextInt(100) < 25 ? 3 : 5;
        Item consumable;
        if (random.nextBoolean()) {
            consumable = new KeyItem();
        } else {
            int heartRoll = random.nextInt(3);
            consumable = switch (heartRoll) {
                case 0 -> new HalfHeartItem();
                case 1 -> new RedHeartItem();
                default -> new DoubleHeartItem();
            };
        }
        itemsForSale.add(new Purchasable(consumable, price));
    }

    public List<Purchasable> getItemsForSale() {
        return itemsForSale;
    }

    @Override
    public boolean isCleared() { return true; }

    @Override
    public void markAsCleared() {}

    @Override
    public boolean isLocked() { return isLocked; }

    @Override
    public void unlock() { this.isLocked = false; }

    @Override
    public String getRoomType() { return "SHOP"; }
}