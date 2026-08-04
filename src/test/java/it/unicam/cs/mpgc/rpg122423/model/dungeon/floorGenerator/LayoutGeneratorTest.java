package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LayoutGeneratorTest {

    @Test
    void testGenerateShapeSize() {
        LayoutGenerator generator = new LayoutGenerator();
        int targetRooms = 10;

        Set<Coordinate> layout = generator.generateShape(targetRooms);

        assertEquals(targetRooms, layout.size(), "Il layout deve contenere esattamente il numero di stanze richiesto.");
        assertTrue(layout.contains(new Coordinate(0, 0)), "Il layout deve sempre includere l'origine (0,0) per lo spawn.");
    }

    @Test
    void testDistanceCalculation() {
        LayoutGenerator generator = new LayoutGenerator();
        Coordinate a = new Coordinate(0, 0);
        Coordinate b = new Coordinate(3, -4);

        // Distanza di Manhattan: |0 - 3| + |0 - (-4)| = 3 + 4 = 7
        assertEquals(7, generator.distance(a, b), "La distanza di Manhattan tra (0,0) e (3,-4) deve essere 7.");
    }

    @Test
    void testFindDeadEnds() {
        LayoutGenerator generator = new LayoutGenerator();
        Coordinate spawn = new Coordinate(0, 0);

        // Costruiamo un layout fittizio a croce incompleta
        Set<Coordinate> layout = Set.of(
                spawn,
                new Coordinate(1, 0), // Vicolo cieco a Est
                new Coordinate(0, 1), // Ramo verso Nord...
                new Coordinate(0, 2)  // ...che finisce in un vicolo cieco
        );

        List<Coordinate> deadEnds = generator.findDeadEnds(layout, spawn);

        assertEquals(2, deadEnds.size(), "Ci devono essere esattamente due vicoli ciechi nel layout fornito.");
        assertTrue(deadEnds.contains(new Coordinate(1, 0)));
        assertTrue(deadEnds.contains(new Coordinate(0, 2)));
        assertFalse(deadEnds.contains(spawn), "Lo spawn non deve mai essere considerato un vicolo cieco valido per le stanze speciali.");
    }
}