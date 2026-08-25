package it.unicam.cs.mpgc.rpg122423.model.combat;

public enum PlayableCharacter {
    KNIGHT("Cavaliere", "/assets/Player.png", 6, 0, 3),
    ROGUE("Ladro", "/assets/DexPlayer.png", 4, 0, 5),
    MAGE("Mago", "/assets/IntPlayer.png", 4, 2, 1);

    private final String displayName;
    private final String spritePath;
    private final int baseHp;
    private final int baseBonusDamage;
    private final int rerollsPerTurn;

    PlayableCharacter(String displayName, String spritePath, int baseHp, int baseBonusDamage, int rerollsPerTurn) {
        this.displayName = displayName;
        this.spritePath = spritePath;
        this.baseHp = baseHp;
        this.baseBonusDamage = baseBonusDamage;
        this.rerollsPerTurn = rerollsPerTurn;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getBaseBonusDamage() {
        return baseBonusDamage;
    }

    public int getRerollsPerTurn() {
        return rerollsPerTurn;
    }
}
