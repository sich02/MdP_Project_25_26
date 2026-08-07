package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor; // Aggiunto import del tuo Floor

public class DungeonService {
    private DungeonLevel currentLevel;
    private final FloorGenerator generator;


    private int currentFloorNumber = 1;

    public DungeonService() {
        this.generator = new FloorGenerator();
    }

    public void startNewRun() {
        System.out.println("Generazione procedurale del Piano " + currentFloorNumber + " in corso...");


        Floor floor = generator.generateFloor(currentFloorNumber);


        Coordinate spawn = new Coordinate(0, 0);


        this.currentLevel = new DungeonLevel(floor.getRooms(), floor.getStartingCoordinate());

        System.out.println("Piano generato e pronto all'esplorazione!");
    }

    public boolean movePlayer(Direction dir) {
        if (currentLevel == null) return false;
        return currentLevel.movePlayer(dir);
    }

    public RoomDTO getCurrentRoomData() {
        if (currentLevel == null) throw new IllegalStateException("Livello non inizializzato");

        Coordinate currentPos = currentLevel.getCurrentPosition();


        return new RoomDTO(
                currentLevel.hasDoor(currentPos, Direction.NORTH),
                currentLevel.hasDoor(currentPos, Direction.SOUTH),
                currentLevel.hasDoor(currentPos, Direction.EAST),
                currentLevel.hasDoor(currentPos, Direction.WEST)
        );
    }
}