package it.unicam.cs.mpgc.rpg122423.model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Pool di tutti gli oggetti disponibili nel gioco.
 * Non contiene più metodi factory statici: la selezione casuale
 * è affidata al service che consuma la pool.
 */
public class ItemPool {
    private final List<Item> hpItems;
    private final List<Item> dmgItems;
    private final List<Item> fireItems;
    private final List<Item> poisonItems;
    private final List<Item> electricItems;
    private final List<Item> allItems;

    public ItemPool() {
        this.hpItems = List.of(
                new HpItem("Blood Bag", "/assets/items/hp/Collectible_Blood_Bag_icon.png"),
                new HpItem("Breakfast", "/assets/items/hp/Collectible_Breakfast_icon.png"),
                new HpItem("<3", "/assets/items/hp/Collectible_Less_Than_Three_icon.png"),
                new HpItem("MEAT!", "/assets/items/hp/Collectible_MEAT21_icon.png")
        );
        this.dmgItems = List.of(
                new DamageItem("Pentagram", "/assets/items/danno/Collectible_Pentagram_icon.png", 2),
                new DamageItem("Glass Eye", "/assets/items/danno/Collectible_Glass_Eye_icon.png", 1),
                new DamageItem("Cricket's Head", "/assets/items/danno/Collectible_Cricket 27s_Head_icon.png", 4),
                new DamageItem("Brimstone", "/assets/items/danno/Collectible_Brimstone_icon.png", 5)
        );
        this.fireItems = List.of(
                new ElementalItem("Bird's Eye", "/assets/items/fuoco/Collectible_Bird27s_Eye_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE),
                new ElementalItem("Fire Mind", "/assets/items/fuoco/Collectible_Fire_Mind_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE),
                new ElementalItem("Ghost Pepper", "/assets/items/fuoco/Collectible_Ghost_Pepper_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE),
                new ElementalItem("Red Candle", "/assets/items/fuoco/Collectible_Red_Candle_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE)
        );
        this.poisonItems = List.of(
                new ElementalItem("Ipecac", "/assets/items/veleno/Collectible_Ipecac_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON),
                new ElementalItem("Serpent's Kiss", "/assets/items/veleno/Collectible_Serpent27s_Kiss_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON),
                new ElementalItem("The Common Cold", "/assets/items/veleno/Collectible_The_Common_Cold_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON),
                new ElementalItem("Toxic Shock", "/assets/items/veleno/Collectible_Toxic_Shock_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON)
        );
        this.electricItems = List.of(
                new ElementalItem("Jacob's Ladder", "/assets/items/electric/Collectible_Jacob 27s_Ladder_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC),
                new ElementalItem("Technology", "/assets/items/electric/Collectible_Technology_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC),
                new ElementalItem("Technology Zero", "/assets/items/electric/Collectible_Technology_Zero_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC),
                new ElementalItem("The Ludovico Technique", "/assets/items/electric/Collectible_The_Ludovico_Technique_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC)
        );

        List<Item> temp = new ArrayList<>();
        temp.addAll(hpItems);
        temp.addAll(dmgItems);
        temp.addAll(fireItems);
        temp.addAll(poisonItems);
        temp.addAll(electricItems);
        this.allItems = Collections.unmodifiableList(temp);
    }

    public List<Item> getAllItems() { return allItems; }
    public List<Item> getHpItems() { return hpItems; }
    public List<Item> getDmgItems() { return dmgItems; }

    /**
     * Restituisce un oggetto casuale dalla pool completa.
     */
    public Item getRandomItem(Random random) {
        return allItems.get(random.nextInt(allItems.size()));
    }

    /**
     * Restituisce un oggetto HP casuale.
     */
    public Item getRandomHpItem(Random random) {
        return hpItems.get(random.nextInt(hpItems.size()));
    }
}
