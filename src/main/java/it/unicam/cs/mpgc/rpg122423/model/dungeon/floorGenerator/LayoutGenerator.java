package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import java.util.*;

public class LayoutGenerator {

    private final Random random = new Random();

    public Set<Coordinate> generateShape(int targetRooms) {
        Set<Coordinate> layout = new HashSet<>();
        List<Coordinate> availableSlots = new ArrayList<>();

        Coordinate spawn = new Coordinate(0, 0);
        layout.add(spawn);
        addEmptyNeighbors(spawn, layout, availableSlots);

        while (layout.size() < targetRooms && !availableSlots.isEmpty()) {
            Coordinate slot = availableSlots.remove(random.nextInt(availableSlots.size()));
            if (layout.contains(slot)) continue;
            if (countNeighbors(slot, layout) > 1 && random.nextDouble() > 0.5) {
                continue;
            }
            layout.add(slot);
            addEmptyNeighbors(slot, layout, availableSlots);
        }

        return layout;
    }

    private void addEmptyNeighbors(Coordinate coord, Set<Coordinate> layout, List<Coordinate> slots) {
        for (Coordinate neighbor : coord.getNeighbors()) {
            if (!layout.contains(neighbor) && !slots.contains(neighbor)) {
                slots.add(neighbor);
            }
        }
    }

    public int countNeighbors(Coordinate coord, Set<Coordinate> layout) {
        int count = 0;
        for (Coordinate neighbor : coord.getNeighbors()) {
            if (layout.contains(neighbor)) count++;
        }
        return count;
    }

    public List<Coordinate> findDeadEnds(Set<Coordinate> layout, Coordinate spawn) {
        List<Coordinate> deadEnds = new ArrayList<>();
        for (Coordinate coord : layout) {
            if (!coord.equals(spawn) && countNeighbors(coord, layout) == 1) {
                deadEnds.add(coord);
            }
        }
        return deadEnds;
    }

    public int distance(Coordinate a, Coordinate b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}