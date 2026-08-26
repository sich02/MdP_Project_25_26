package it.unicam.cs.mpgc.rpg122423.model.status;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;

/**
 * Effetto bruciatura: infligge danni al nemico ad ogni turno.
 */
public class BurnEffect extends BaseStatusEffect {
    public static final String EFFECT_NAME = "Bruciatura";
    private final int damagePerTurn;
    private final Enemy target;

    public BurnEffect(Enemy target, int damagePerTurn) {
        super("Bruciatura", 3);
        this.target = target;
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void tick() {
        if (!isExpired()) {
            target.takeDamage(damagePerTurn);
            super.tick();
            System.out.println("🔥 Burn tick! " + target.getName() + " subisce " + damagePerTurn + " danni. (Turni rimanenti: " + getRemainingTurns() + ")");
        }
    }
}
