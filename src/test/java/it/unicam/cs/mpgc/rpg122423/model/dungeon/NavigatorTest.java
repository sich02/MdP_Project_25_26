package it.unicam.cs.mpgc.rpg122423.model.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.CombatRoom;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.SpawnRoom;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class NavigatorTest {

    @Test
    void testNavigationAndLocking() {
        Map<Coordinate, Room> map = new HashMap<>();
        map.put(new Coordinate(0, 0), new SpawnRoom());
        CombatRoom hostileRoom = new CombatRoom(false);
        map.put(new Coordinate(0, 1), hostileRoom);

        Floor floor = new Floor(1, map);

        Navigator navigator = new Navigator(floor);
        List<Direction> doors = navigator.getAvailableDoors();

        assertTrue(doors.contains(Direction.NORTH));

        navigator.move(Direction.NORTH);

        assertEquals(new Coordinate(0, 1), navigator.getCurrentPosition());
        assertTrue(navigator.getAvailableDoors().isEmpty());

        hostileRoom.resolveEncounter();

        assertTrue(navigator.getAvailableDoors().contains(Direction.SOUTH));
    }
}