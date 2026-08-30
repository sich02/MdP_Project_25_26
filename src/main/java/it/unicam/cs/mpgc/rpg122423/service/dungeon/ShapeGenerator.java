package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;

import java.util.*;

public class ShapeGenerator {

    private static final double BRANCH_THRESHOLD = 0.5;

    private final Random random;

    public ShapeGenerator(Random random) {
        this.random = random;
    }

    public Set<Coordinate> generateShape(int floorNumber) {
        int targetRooms = calculateTotalRooms(floorNumber);
        Set<Coordinate> layout = new HashSet<>();
        List<Coordinate> availableSlots = new ArrayList<>();

        Coordinate spawn = new Coordinate(0, 0);
        layout.add(spawn);
        addEmptyNeighbors(spawn, layout, availableSlots);

        while (layout.size() < targetRooms && !availableSlots.isEmpty()) {
            Coordinate slot = availableSlots.remove(random.nextInt(availableSlots.size()));
            if (layout.contains(slot)) continue;

            if (slot.countNeighborsIn(layout) > 1 && random.nextDouble() > BRANCH_THRESHOLD) {
                continue;
            }

            layout.add(slot);
            addEmptyNeighbors(slot, layout, availableSlots);
        }

        return layout;
    }

    private int calculateTotalRooms(int floorNumber) {
        int randomBase = random.nextInt(2) + 5;
        return (int) Math.floor(3.33 * floorNumber) + randomBase;
    }

    private void addEmptyNeighbors(Coordinate coord, Set<Coordinate> layout, List<Coordinate> slots) {
        for (Coordinate neighbor : coord.getNeighbors()) {
            if (!layout.contains(neighbor) && !slots.contains(neighbor)) {
                slots.add(neighbor);
            }
        }
    }
}