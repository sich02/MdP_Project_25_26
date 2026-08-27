package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.model.dice.Element;
import javafx.scene.paint.Color;

/**
 * Helper per i colori degli elementi nel layer View/Controller.
 * I colori sono specifici della UI e non appartengono al model.
 */
public class ElementColorHelper {

    public static Color getColor(Element element) {
        return switch (element) {
            case FIRE -> Color.ORANGERED;
            case POISON -> Color.LIMEGREEN;
            case ELECTRIC -> Color.CYAN;
            case NONE -> Color.WHITE;
        };
    }
}
