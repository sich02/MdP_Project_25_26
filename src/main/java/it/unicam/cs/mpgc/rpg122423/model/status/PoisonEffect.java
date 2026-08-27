package it.unicam.cs.mpgc.rpg122423.model.status;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;

/**
 * Effetto avvelenamento: infligge danni al nemico ad ogni turno.
 */
public class PoisonEffect implements StatusEffect {
    public static final String EFFECT_NAME = "Avvelenamento";
    private final int damagePerTurn;
    private final Enemy target;
    private int remainingTurns;

    public PoisonEffect(Enemy target, int damagePerTurn) {
        this.target = target;
        this.damagePerTurn = damagePerTurn;
        this.remainingTurns = 3;
    }

    @Override
    public String getName() { return EFFECT_NAME; }

    @Override
    public int getRemainingTurns() { return remainingTurns; }

    @Override
    public void tick() {
        if (!isExpired()) {
            target.takeDamage(damagePerTurn);
            remainingTurns--;
            System.out.println("☠️ Tic di Veleno! " + target.getName() + " subisce " + damagePerTurn + " danni. (Turni rimanenti: " + remainingTurns + ")");
        }
    }

    @Override
    public boolean isExpired() { return remainingTurns <= 0; }
}
