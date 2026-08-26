package it.unicam.cs.mpgc.rpg122423.model.dice;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;
import it.unicam.cs.mpgc.rpg122423.model.status.BurnEffect;
import it.unicam.cs.mpgc.rpg122423.model.status.PoisonEffect;
import javafx.scene.paint.Color;

public enum Element {
    NONE("Nessuno", Color.WHITE),
    FIRE("Fuoco", Color.ORANGERED),
    POISON("Veleno", Color.LIMEGREEN),
    ELECTRIC("Elettro", Color.CYAN);

    private final String displayName;
    private final Color color;

    Element(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public Color getColor() { return color; }

    /**
     * Crea un effetto di stato associato a questo elemento, se applicabile.
     * @param target il nemico bersaglio
     * @param damage il danno per turno
     * @return l'effetto di stato, o null se l'elemento non ne genera
     */
    public StatusEffect createStatusEffect(Enemy target, int damage) {
        return switch (this) {
            case FIRE -> new BurnEffect(target, damage);
            case POISON -> new PoisonEffect(target, damage);
            default -> null;
        };
    }
}
