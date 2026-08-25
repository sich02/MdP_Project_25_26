package it.unicam.cs.mpgc.rpg122423.model.item;

import java.util.List;
import java.util.Random;

public class ItemPool {
    private static final List<Item> HP_ITEMS = List.of(
            new HpItem("Blood Bag", "/assets/items/hp/Collectible_Blood_Bag_icon.png"),
            new HpItem("Breakfast", "/assets/items/hp/Collectible_Breakfast_icon.png"),
            new HpItem("<3", "/assets/items/hp/Collectible_Less_Than_Three_icon.png"),
            new HpItem("MEAT!", "/assets/items/hp/Collectible_MEAT21_icon.png")
    );

    private static final List<Item> DMG_ITEMS = List.of(
            new DamageItem("Pentagram", "/assets/items/danno/Collectible_Pentagram_icon.png", 2),
            new DamageItem("Glass Eye", "/assets/items/danno/Collectible_Glass_Eye_icon.png", 1),
            new DamageItem("Cricket's Head", "/assets/items/danno/Collectible_Cricket 27s_Head_icon.png", 4),
            new DamageItem("Brimstone", "/assets/items/danno/Collectible_Brimstone_icon.png", 5)
    );

    private static final List<Item> FIRE_ITEMS = List.of(
            new ElementalItem("Bird's Eye", "/assets/items/fuoco/Collectible_Bird27s_Eye_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE),
            new ElementalItem("Fire Mind", "/assets/items/fuoco/Collectible_Fire_Mind_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE),
            new ElementalItem("Ghost Pepper", "/assets/items/fuoco/Collectible_Ghost_Pepper_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE),
            new ElementalItem("Red Candle", "/assets/items/fuoco/Collectible_Red_Candle_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE)
    );

    private static final List<Item> POISON_ITEMS = List.of(
            new ElementalItem("Ipecac", "/assets/items/veleno/Collectible_Ipecac_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON),
            new ElementalItem("Serpent's Kiss", "/assets/items/veleno/Collectible_Serpent27s_Kiss_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON),
            new ElementalItem("The Common Cold", "/assets/items/veleno/Collectible_The_Common_Cold_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON),
            new ElementalItem("Toxic Shock", "/assets/items/veleno/Collectible_Toxic_Shock_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON)
    );

    private static final List<Item> ELECTRIC_ITEMS = List.of(
            new ElementalItem("Jacob's Ladder", "/assets/items/electric/Collectible_Jacob 27s_Ladder_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC),
            new ElementalItem("Technology", "/assets/items/electric/Collectible_Technology_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC),
            new ElementalItem("Technology Zero", "/assets/items/electric/Collectible_Technology_Zero_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC),
            new ElementalItem("The Ludovico Technique", "/assets/items/electric/Collectible_The_Ludovico_Technique_icon.png", it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC)
    );

    private static final List<Item> ALL_ITEMS = new java.util.ArrayList<>();
    static {
        ALL_ITEMS.addAll(HP_ITEMS);
        ALL_ITEMS.addAll(DMG_ITEMS);
        ALL_ITEMS.addAll(FIRE_ITEMS);
        ALL_ITEMS.addAll(POISON_ITEMS);
        ALL_ITEMS.addAll(ELECTRIC_ITEMS);
    }

    private static final Random RANDOM = new Random();

    public static Item getRandomItem() {
        return ALL_ITEMS.get(RANDOM.nextInt(ALL_ITEMS.size()));
    }

    public static Item getRandomHpItem() {
        return HP_ITEMS.get(RANDOM.nextInt(HP_ITEMS.size()));
    }
}
