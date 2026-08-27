package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dice.Element;

/**
 * Oggetto che assegna un elemento a un dado a scelta del giocatore.
 * L'assegnazione effettiva avviene nel Controller dopo la selezione del dado.
 */
public class ElementalItem implements Item {
    private final String name;
    private final String imagePath;
    private final Element element;

    public ElementalItem(String name, String imagePath, Element element) {
        this.name = name;
        this.imagePath = imagePath;
        this.element = element;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getImagePath() { return imagePath; }

    public Element getElement() { return element; }

    /**
     * L'ElementalItem non applica direttamente l'effetto: l'assegnazione
     * dell'elemento al dado avviene tramite il Controller dopo la selezione.
     * Questo metodo è intenzionalmente vuoto — il contratto di Item.onPickup()
     * è "applica l'effetto dell'oggetto", ma per gli oggetti elementali
     * l'effetto richiede una scelta dell'utente (quale dado incantare).
     */
    @Override
    public void onPickup(Player player) {
        // Nessun effetto immediato: richiede interazione utente (selezione dado)
    }
}
