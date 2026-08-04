package it.unicam.cs.mpgc.rpg122423.model.dungeon;

public class Floor {
    private final int floorNumber;
    private boolean isCleared;

    public Floor(int floorNumber) {
        if(floorNumber <=0){
            throw new IllegalArgumentException("Il numero del piano non puó essere minore di 0");
        }
        this.floorNumber = floorNumber;
        this.isCleared = false;
    }

    public int  getFloorNumber() {return floorNumber;}
    public boolean isCleared() {return isCleared;}
    public void markAsCleared(){this.isCleared = true;}
}
