package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class ShopRoom implements Room, Lockable {
    private boolean isLocked;

    public ShopRoom(boolean requiresKey) {this.isLocked = requiresKey;}

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