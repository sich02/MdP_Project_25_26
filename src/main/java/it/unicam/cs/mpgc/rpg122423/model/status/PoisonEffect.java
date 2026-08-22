package it.unicam.cs.mpgc.rpg122423.model.status;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;

public class PoisonEffect implements StatusEffect {
    private int remainingTurns;
    private final int damagePerTurn;
    private final Enemy target;

    public PoisonEffect(Enemy target, int damagePerTurn) {
        this.target = target;
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = 3;
    }

    @Override
    public String getName() {
        return "Avvelenamento";
    }

    @Override
    public int getRemainingTurns() {
        return remainingTurns;
    }

    @Override
    public void tick() {
        if (!isExpired()) {
            target.takeDamage(damagePerTurn);
            remainingTurns--;
            System.out.println("☠️ Poison tick! " + target.getName() + " subisce " + damagePerTurn + " danni. (Turni rimanenti: " + remainingTurns + ")");
        }
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }
}
