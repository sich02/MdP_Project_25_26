package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.item.Item;

/**
 * Stanza del tesoro. Contiene un oggetto fornito dall'esterno.
 */
public class TreasureRoom implements Room, Lootable, Lockable {
    private boolean cleared;
    private boolean isLocked;
    private boolean lootAvailable;
    private final Item lootItem;

    /**
     * Crea una stanza del tesoro con il loot iniettato dall'esterno.
     *
     * @param requiresKey se la stanza richiede una chiave per entrare
     * @param lootItem    l'oggetto di loot generato dall'esterno
     */
    public TreasureRoom(boolean requiresKey, Item lootItem) {
        this.isLocked = requiresKey;
        this.cleared = false;
        this.lootAvailable = true;
        this.lootItem = lootItem;
    }

    @Override
    public boolean isCleared() { return cleared; }

    @Override
    public void markAsCleared() { this.cleared = true; }

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
    public RoomType getRoomType() { return RoomType.TREASURE; }
}
