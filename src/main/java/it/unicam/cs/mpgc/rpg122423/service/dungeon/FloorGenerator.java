package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import java.util.Map;

public class FloorGenerator {
    private final LayoutGenerator layoutGenerator;

    public FloorGenerator() {
        this.layoutGenerator = new LayoutGenerator();
    }

    public Floor generateFloor(int floorNumber) {
        Map<Coordinate, Room> layout = layoutGenerator.generateLayout(floorNumber);
        return new Floor(floorNumber, layout);
    }
}
