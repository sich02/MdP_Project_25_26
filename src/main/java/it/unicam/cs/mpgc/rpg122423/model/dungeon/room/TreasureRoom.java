package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.item.Item;
import it.unicam.cs.mpgc.rpg122423.model.item.ItemPool;

public class TreasureRoom implements Room, Lootable, Lockable {
    private boolean cleared;
    private boolean isLocked;
    private boolean lootAvailable;
    private final Item lootItem;

    public TreasureRoom(boolean requiresKey) {
        this.isLocked = requiresKey;
        this.cleared = false;
        this.lootAvailable = true;
        this.lootItem = ItemPool.getRandomItem();
    }

    @Override
    public boolean isCleared() { return cleared; }

    @Override
    public void markAsCleared(){this.cleared = true;}

    @Override
    public boolean isLocked() { return isLocked; }

    @Override
    public void unlock() { this.isLocked = false; }

    @Override
    public boolean hasLoot() { return lootAvailable; }

    @Override
    public Item getLoot() { return lootItem; }

    @Override
    public void claimLoot() {
        this.lootAvailable = false;
        this.markAsCleared();
    }

    @Override
    public String getRoomType() { return "TREASURE"; }
}

