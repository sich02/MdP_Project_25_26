package it.unicam.cs.mpgc.rpg122423.model.dice;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {

    private static final int faces = 6;
    private int currentValue;
    private Element element;

    public Dice(){
        this.element = Element.NONE;
        this.roll();
    }

    public void roll(){
        this.currentValue = ThreadLocalRandom.current().nextInt(1, faces+1);
    }

    public int getFaces() {
        return faces;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }


}
