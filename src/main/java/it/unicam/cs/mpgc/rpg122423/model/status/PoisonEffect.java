package it.unicam.cs.mpgc.rpg122423.model.status;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;

/**
 * Effetto avvelenamento: infligge danni al nemico ad ogni turno.
 */
public class PoisonEffect extends BaseStatusEffect {
    public static final String EFFECT_NAME = "Avvelenamento";
    private final int damagePerTurn;
    private final Enemy target;

    public PoisonEffect(Enemy target, int damagePerTurn) {
        super("Avvelenamento", 3);
        this.target = target;
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void tick() {
        if (!isExpired()) {
            target.takeDamage(damagePerTurn);
            super.tick();
            System.out.println("☠️ Tic di Veleno! " + target.getName() + " subisce " + damagePerTurn + " danni. (Turni rimanenti: " + getRemainingTurns() + ")");
        }
    }
}
