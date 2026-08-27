package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public class SpawnRoom implements Room {
    private boolean cleared = true;

    @Override
    public boolean isCleared() { return cleared; }

    @Override
    public void markAsCleared() { this.cleared = true; }

    @Override
    public RoomType getRoomType() { return RoomType.NORMAL; }
}
