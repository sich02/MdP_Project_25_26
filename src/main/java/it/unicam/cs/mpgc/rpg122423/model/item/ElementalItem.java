package it.unicam.cs.mpgc.rpg122423.model.item;

import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dice.Element;

/**
 * Oggetto che assegna un elemento a un dado a scelta del giocatore.
 */
public class ElementalItem extends Item {
    private final Element element;

    public ElementalItem(String name, String imagePath, Element element) {
        super(name, imagePath);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public void onPickup(Player player) {
        // L'assegnazione avviene tramite la UI (DungeonController), che invocherà il metodo appropriato.
        // Questo metodo rimane vuoto, oppure possiamo loggare.
        System.out.println("Hai raccolto un oggetto elementale: " + getName() + ". Attendo la selezione del dado.");
    }
}
