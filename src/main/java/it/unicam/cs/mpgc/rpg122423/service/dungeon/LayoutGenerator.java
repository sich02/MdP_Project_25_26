package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import java.util.*;

public class LayoutGenerator {

    private final ShapeGenerator shapeGenerator;
    private final TopologicalAnalyzer topologicalAnalyzer;
    private final Random random;

    public LayoutGenerator() {
        this.shapeGenerator = new ShapeGenerator();
        this.topologicalAnalyzer = new TopologicalAnalyzer();
        this.random = new Random();
    }

    public Map<Coordinate, Room> generateLayout(int floorNumber) {
        Set<Coordinate> shape = shapeGenerator.generateShape(floorNumber);
        Coordinate spawn = new Coordinate(0, 0);
        Map<Coordinate, Integer> distances = topologicalAnalyzer.calculateDistances(shape, spawn);
        List<Coordinate> deadEnds = topologicalAnalyzer.findDeadEnds(shape, spawn);
        Coordinate bossRoomCoord = spawn;
        int maxDist = -1;

        for (Map.Entry<Coordinate, Integer> entry : distances.entrySet()) {
            if (entry.getValue() > maxDist) {
                maxDist = entry.getValue();
                bossRoomCoord = entry.getKey();
            }
        }

        deadEnds.remove(bossRoomCoord);
        Collections.shuffle(deadEnds, random);
        Coordinate treasureCoord = !deadEnds.isEmpty() ? deadEnds.remove(0) : null;
        Coordinate shopCoord = !deadEnds.isEmpty() ? deadEnds.remove(0) : null;
        Map<Coordinate, Room> layout = new HashMap<>();
        layout.put(spawn, new SpawnRoom());
        layout.put(bossRoomCoord, new BossRoom(floorNumber));

        if (treasureCoord != null) {
            boolean requiresKey = floorNumber >= 2;
            layout.put(treasureCoord, new TreasureRoom(requiresKey));
        }

        if (shopCoord != null) {
            boolean requiresKey = floorNumber >= 2;
            layout.put(shopCoord, new ShopRoom(requiresKey));
        }

        for (Coordinate coord : shape) {
            boolean generatesLoot = random.nextBoolean();
            layout.putIfAbsent(coord, new CombatRoom(generatesLoot, floorNumber));
        }

        return layout;
    }
}