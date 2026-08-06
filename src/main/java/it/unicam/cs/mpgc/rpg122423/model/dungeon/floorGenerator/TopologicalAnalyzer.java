package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import java.util.*;

public class TopologicalAnalyzer {

    public Map<Coordinate, Integer> calculateDistances(Set<Coordinate> layout, Coordinate start) {
        Map<Coordinate, Integer> distances = new HashMap<>();
        Queue<Coordinate> queue = new LinkedList<>();

        queue.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = distances.get(current);

            for (Coordinate neighbor : current.getNeighbors()) {
                if (layout.contains(neighbor) && !distances.containsKey(neighbor)) {
                    distances.put(neighbor, currentDist + 1);
                    queue.add(neighbor);
                }
            }
        }
        return distances;
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

    private int countNeighbors(Coordinate coord, Set<Coordinate> layout) {
        int count = 0;
        for (Coordinate neighbor : coord.getNeighbors()) {
            if (layout.contains(neighbor)) count++;
        }
        return count;
    }
}