package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import java.util.Map;

import java.util.Random;

public class FloorGenerator {

    public FloorGenerator() {
    }

    public Floor generateFloor(int floorNumber) {
        long seed = new Random().nextLong();
        return generateFloorWithSeed(floorNumber, seed);
    }

    public Floor generateFloorWithSeed(int floorNumber, long seed) {
        Random random = new Random(seed);
        LayoutGenerator layoutGenerator = new LayoutGenerator(random);
        Map<Coordinate, Room> layout = layoutGenerator.generateLayout(floorNumber);
        return new Floor(floorNumber, layout, seed);
    }
}
