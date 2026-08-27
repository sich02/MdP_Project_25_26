package it.unicam.cs.mpgc.rpg122423.model.dice;

/**
 * Rappresenta un elemento associabile ai dadi.
 * Non contiene logica di creazione effetti (spostata in CombatService)
 * né dipendenze da JavaFX.
 */
public enum Element {
    NONE("Nessuno"),
    FIRE("Fuoco"),
    POISON("Veleno"),
    ELECTRIC("Elettro");

    private final String displayName;

    Element(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
