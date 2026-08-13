package it.unicam.cs.mpgc.rpg122423.model.dungeon.room;

import it.unicam.cs.mpgc.rpg122423.model.item.Item;

public interface Lootable {
    boolean hasLoot();
    Item getLoot();
    void claimLoot();
}
