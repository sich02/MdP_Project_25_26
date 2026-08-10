package it.unicam.cs.mpgc.rpg122423.model.combat;

import java.util.List;

/**
 * Risultato della valutazione di una combo di dadi.
 * 
 * @param name         Nome della combo (es. "TRIS", "POKER!")
 * @param totalDamage  Danno totale calcolato
 * @param comboIndices Indici dei dadi che formano la combo (vuoto se nessuna combo)
 */
public record ComboResult(String name, int totalDamage, List<Integer> comboIndices) {

    /** Costruttore di compatibilità senza indici combo. */
    public ComboResult(String name, int totalDamage) {
        this(name, totalDamage, List.of());
    }
}