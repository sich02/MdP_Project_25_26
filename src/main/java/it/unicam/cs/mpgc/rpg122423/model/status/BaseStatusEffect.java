package it.unicam.cs.mpgc.rpg122423.model.status;

public class BaseStatusEffect implements StatusEffect {
    private final String name;
    private int remainingTurns;

    public BaseStatusEffect(String name, int durationTurns) {
        this.name = name;
        this.remainingTurns = durationTurns;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRemainingTurns() {
        return remainingTurns;
    }

    @Override
    public void tick() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}