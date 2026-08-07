package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LayoutGeneratorTest {

    @Test
    void testGenerateShapeSize() {
        ShapeGenerator generator = new ShapeGenerator();
        int floorNumber = 1;

        Set<Coordinate> layout = generator.generateShape(floorNumber);

        assertTrue(layout.size() == 8 || layout.size() == 9, "Il layout per il piano 1 deve contenere 8 o 9 stanze in base al seed randomico.");
        assertTrue(layout.contains(new Coordinate(0, 0)), "Il layout deve sempre includere l'origine (0,0) per lo spawn.");
    }

    @Test
    void testDistanceCalculation() {
        TopologicalAnalyzer analyzer = new TopologicalAnalyzer();

        Coordinate start = new Coordinate(0, 0);
        Coordinate middle = new Coordinate(1, 0);
        Coordinate end = new Coordinate(2, 0);

        Set<Coordinate> layout = Set.of(start, middle, end);

        Map<Coordinate, Integer> distances = analyzer.calculateDistances(layout, start);

        assertEquals(0, distances.get(start), "La distanza dal punto di partenza a se stesso è 0.");
        assertEquals(1, distances.get(middle), "La stanza direttamente adiacente dista 1 passo.");
        assertEquals(2, distances.get(end), "La stanza in fondo al corridoio dista 2 passi.");
    }

    @Test
    void testFindDeadEnds() {
        TopologicalAnalyzer analyzer = new TopologicalAnalyzer();
        Coordinate spawn = new Coordinate(0, 0);

        Set<Coordinate> layout = Set.of(
                spawn,
                new Coordinate(1, 0),
                new Coordinate(0, 1),
                new Coordinate(0, 2)
        );

        List<Coordinate> deadEnds = analyzer.findDeadEnds(layout, spawn);

        assertEquals(2, deadEnds.size(), "Ci devono essere esattamente due vicoli ciechi nel layout fornito.");
        assertTrue(deadEnds.contains(new Coordinate(1, 0)));
        assertTrue(deadEnds.contains(new Coordinate(0, 2)));
        assertFalse(deadEnds.contains(spawn), "Lo spawn non deve mai essere considerato un vicolo cieco valido.");
    }
}