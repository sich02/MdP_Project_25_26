package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class TreasureRoom implements Room,  Lootable, Lockable {
    private boolean cleared;
    private boolean isLocked;
    private boolean lootAvailable;

    public TreasureRoom(boolean requiresKey) {
        this.isLocked = requiresKey;
        this.cleared = false;
        this.lootAvailable = true;
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
    public void claimLoot() {
        this.lootAvailable = false;
        this.markAsCleared();
    }

    @Override
    public String getRoomType() { return "TREASURE"; }
}

