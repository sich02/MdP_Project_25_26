package it.unicam.cs.mpgc.rpg122423.model.dungeon.floorGenerator;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FloorGeneratorTest {

    @Test
    void testRoomCountAndSpawnPlacement() {
        FloorGenerator generator = new FloorGenerator();
        int floorNumber = 1;
        // Formula: 5 + (1 * 2) = 7
        Map<Coordinate, Room> floor = generator.generateFloor(floorNumber);

        assertEquals(7, floor.size(), "Al piano 1 devono esserci esattamente 7 stanze in base alla formula.");
        assertInstanceOf(SpawnRoom.class, floor.get(new Coordinate(0, 0)), "Alle coordinate (0,0) deve esserci tassativamente la SpawnRoom.");
    }

    @Test
    void testSpecialRoomsPresence() {
        FloorGenerator generator = new FloorGenerator();
        Map<Coordinate, Room> floor = generator.generateFloor(3); // Testiamo su un piano profondo per avere abbastanza stanze

        long bossCount = floor.values().stream().filter(r -> r instanceof BossRoom).count();
        long shopCount = floor.values().stream().filter(r -> r instanceof ShopRoom).count();
        long treasureCount = floor.values().stream().filter(r -> r instanceof TreasureRoom).count();

        assertEquals(1, bossCount, "Deve esserci generata esattamente una BossRoom.");
        assertEquals(1, shopCount, "Deve esserci generata esattamente una ShopRoom.");
        assertEquals(1, treasureCount, "Deve esserci generata esattamente una TreasureRoom.");
    }

    @Test
    void testTreasureRoomLockLogicByFloor() {
        FloorGenerator generator = new FloorGenerator();

        // Test Piano 1
        Map<Coordinate, Room> floor1 = generator.generateFloor(1);
        TreasureRoom tRoom1 = (TreasureRoom) floor1.values().stream()
                .filter(r -> r instanceof TreasureRoom).findFirst()
                .orElseThrow(() -> new AssertionError("TreasureRoom mancante al piano 1"));

        assertFalse(tRoom1.isLocked(), "Al piano 1 la TreasureRoom non deve MAI richiedere una chiave.");

        // Test Piano 2
        Map<Coordinate, Room> floor2 = generator.generateFloor(2);
        TreasureRoom tRoom2 = (TreasureRoom) floor2.values().stream()
                .filter(r -> r instanceof TreasureRoom).findFirst()
                .orElseThrow(() -> new AssertionError("TreasureRoom mancante al piano 2"));

        assertTrue(tRoom2.isLocked(), "Dal piano 2 in poi la TreasureRoom DEVE richiedere una chiave.");
    }
}