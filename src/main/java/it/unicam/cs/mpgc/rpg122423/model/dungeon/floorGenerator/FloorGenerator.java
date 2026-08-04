package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import java.util.*;
public class FloorGenerator {

    private final LayoutGenerator layoutGenerator;
    private final Random random = new Random();

    public FloorGenerator() {
        this.layoutGenerator = new LayoutGenerator();
    }

    public Map<Coordinate, Room> generateFloor(int floorNumber) {
        int targetRooms = 5 + (floorNumber * 2);
        Set<Coordinate> skeleton = layoutGenerator.generateShape(targetRooms);
        return populateRooms(skeleton, floorNumber);
    }

    private Map<Coordinate, Room> populateRooms(Set<Coordinate> skeleton, int floorNumber) {
        Map<Coordinate, Room> floorMap = new HashMap<>();
        Coordinate spawn = new Coordinate(0, 0);
        for (Coordinate coord : skeleton) {
            floorMap.put(coord, new CombatRoom(true));
        }
        floorMap.put(spawn, new SpawnRoom());
        List<Coordinate> deadEnds = layoutGenerator.findDeadEnds(skeleton, spawn);
        if (deadEnds.isEmpty()) {
            deadEnds = new ArrayList<>(skeleton);
            deadEnds.remove(spawn);
        }
        assignSpecialRooms(floorMap, deadEnds, spawn, floorNumber);
        return floorMap;
    }

    private void assignSpecialRooms(Map<Coordinate, Room> floorMap, List<Coordinate> deadEnds, Coordinate spawn, int floorNumber) {
        deadEnds.sort(Comparator.comparingInt(c -> -layoutGenerator.distance(spawn, c)));
        Coordinate bossCoord = deadEnds.remove(0);
        floorMap.put(bossCoord, new BossRoom());

        if (!deadEnds.isEmpty()) {
            Coordinate treasureCoord = deadEnds.remove(random.nextInt(deadEnds.size()));
            boolean requiresKey = floorNumber > 1;
            floorMap.put(treasureCoord, new TreasureRoom(requiresKey));
        }

        if (!deadEnds.isEmpty()) {
            Coordinate shopCoord = deadEnds.remove(random.nextInt(deadEnds.size()));
            floorMap.put(shopCoord, new ShopRoom());
        }
    }


}
