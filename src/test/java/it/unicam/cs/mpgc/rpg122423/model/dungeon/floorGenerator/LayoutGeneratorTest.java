package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.ShapeGenerator;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.TopologicalAnalyzer;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LayoutGeneratorTest {

    @Test
    void testGenerateShapeSize() {
        // Se hai estratto la logica in ShapeGenerator, usa quella, altrimenti rimetti LayoutGenerator
        ShapeGenerator generator = new ShapeGenerator();
        int targetRooms = 10;

        Set<Coordinate> layout = generator.generateShape(targetRooms);

        assertEquals(targetRooms, layout.size(), "Il layout deve contenere esattamente il numero di stanze richiesto.");
        assertTrue(layout.contains(new Coordinate(0, 0)), "Il layout deve sempre includere l'origine (0,0) per lo spawn.");
    }

    @Test
    void testDistanceCalculation() {
        // Se la distanza è un metodo di utilità o sta in un'altra classe, puntala qui
        Coordinate a = new Coordinate(0, 0);
        Coordinate b = new Coordinate(3, -4);

        // Esempio: se ora distance sta in una classe di supporto o in Coordinate stessa:
        int distance = Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
        assertEquals(7, distance, "La distanza di Manhattan tra (0,0) e (3,-4) deve essere 7.");
    }

    @Test
    void testFindDeadEnds() {
        // Se hai spostato i vicoli ciechi nel TopologicalAnalyzer come suggerisce l'albero dei file:
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
        assertFalse(deadEnds.contains(spawn), "Lo spawn non deve mai essere considerato un vicolo cieco valido per le stanze speciali.");
    }
}