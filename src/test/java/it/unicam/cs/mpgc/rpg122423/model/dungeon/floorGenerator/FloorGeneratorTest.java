package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.FloorGenerator;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FloorGeneratorTest {

    @Test
    void testRoomCountAndSpawnPlacement() {
        FloorGenerator generator = new FloorGenerator();

        Floor generatedFloor = generator.generateFloor(1);
        Map<Coordinate, Room> floorMap = generatedFloor.getRooms();

        assertTrue(floorMap.size() == 8 || floorMap.size() == 9, "Al piano 1 devono esserci 8 o 9 stanze in base alla formula randomica.");
        assertInstanceOf(SpawnRoom.class, floorMap.get(new Coordinate(0, 0)), "Alle coordinate (0,0) deve esserci tassativamente la SpawnRoom.");
    }

    @Test
    void testSpecialRoomsPresence() {
        FloorGenerator generator = new FloorGenerator();

        Floor generatedFloor = generator.generateFloor(3);
        Map<Coordinate, Room> floorMap = generatedFloor.getRooms();

        long bossCount = floorMap.values().stream().filter(r -> r instanceof BossRoom).count();
        long shopCount = floorMap.values().stream().filter(r -> r instanceof ShopRoom).count();
        long treasureCount = floorMap.values().stream().filter(r -> r instanceof TreasureRoom).count();

        assertEquals(1, bossCount, "Deve esserci generata sempre esattamente una BossRoom.");
        assertTrue(shopCount <= 1, "La ShopRoom viene generata solo se c'è un vicolo cieco disponibile (max 1).");
        assertTrue(treasureCount <= 1, "La TreasureRoom viene generata solo se c'è un vicolo cieco disponibile (max 1).");
    }

    @Test
    void testTreasureRoomLockLogicByFloor() {
        FloorGenerator generator = new FloorGenerator();
        TreasureRoom tRoom1 = null;
        while (tRoom1 == null) {
            Map<Coordinate, Room> floor1 = generator.generateFloor(1).getRooms();
            tRoom1 = (TreasureRoom) floor1.values().stream()
                    .filter(r -> r instanceof TreasureRoom).findFirst().orElse(null);
        }
        assertFalse(tRoom1.isLocked(), "Al piano 1 la TreasureRoom non deve MAI richiedere una chiave.");

        // Stessa cosa per il piano 2
        TreasureRoom tRoom2 = null;
        while (tRoom2 == null) {
            Map<Coordinate, Room> floor2 = generator.generateFloor(2).getRooms();
            tRoom2 = (TreasureRoom) floor2.values().stream()
                    .filter(r -> r instanceof TreasureRoom).findFirst().orElse(null);
        }
        assertTrue(tRoom2.isLocked(), "Dal piano 2 in poi la TreasureRoom DEVE richiedere una chiave.");
    }
}