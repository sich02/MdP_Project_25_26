package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

public interface Room {
    boolean isCleared();
    void markAsCleared();

    /** Restituisce il tipo di questa stanza per la rappresentazione visiva. */
    String getRoomType();
}

