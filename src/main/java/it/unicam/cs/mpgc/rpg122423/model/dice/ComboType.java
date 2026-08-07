package it.unicam.cs.mpgc.rpg122423.model.dice;

public enum ComboType {
    HIGH_CARD(1),
    PAIR(2),
    TWO_PAIR(4),
    THREE_OF_A_KIND(5),
    STRAIGHT(7),
    FULL_HOUSE(9),
    FOUR_OF_A_KIND(12),
    YAHTZEE(20);

    private final int baseDamage;

    ComboType(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public int getBaseDamage() {
        return baseDamage;
    }
}