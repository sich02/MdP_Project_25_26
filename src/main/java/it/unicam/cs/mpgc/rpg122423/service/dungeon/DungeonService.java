package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.Coordinate;
import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.dto.PlayerDTO;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;

public class DungeonService {
    private DungeonLevel currentLevel;
    private final FloorGenerator generator;
    private Player player;


    private int currentFloorNumber = 1;

    public DungeonService() {
        this.generator = new FloorGenerator();
    }

    public void startNewRun() {
        System.out.println("Generazione procedurale del Piano " + currentFloorNumber + " in corso...");
        this.player = new Player();
        Floor floor = generator.generateFloor(currentFloorNumber);
        Coordinate spawn = new Coordinate(0, 0);
        this.currentLevel = new DungeonLevel(floor.getRooms(), floor.getStartingCoordinate());
        System.out.println("Piano generato e pronto all'esplorazione!");
    }

    public boolean movePlayer(Direction dir) {
        if (currentLevel == null) return false;
        return currentLevel.movePlayer(dir);
    }

    public RoomDTO getCurrentRoomData() {
        if (currentLevel == null) throw new IllegalStateException("Livello non inizializzato");
        Coordinate currentPos = currentLevel.getCurrentPosition();

        return new RoomDTO(
                inspectDoor(currentPos, Direction.NORTH),
                inspectDoor(currentPos, Direction.SOUTH),
                inspectDoor(currentPos, Direction.EAST),
                inspectDoor(currentPos, Direction.WEST)
        );
    }

    private DoorDTO inspectDoor(Coordinate currentPos, Direction dir) {
        Coordinate targetPos = currentPos.moveTo(dir);
        Room adjacentRoom = currentLevel.getRoomAt(targetPos);

        if (adjacentRoom == null) {
            return new DoorDTO(false, "NONE", false);
        }

        String type = "NORMAL";
        boolean locked = false;
        if (adjacentRoom instanceof BossRoom) {
            type = "BOSS";
        } else if (adjacentRoom instanceof TreasureRoom) {
            type = "TREASURE";
        } else if (adjacentRoom instanceof ShopRoom) {
            type = "SHOP";
        }

        if (adjacentRoom instanceof Lockable lockableRoom) {
            locked = lockableRoom.isLocked();
        }

        return new DoorDTO(true, type, locked);
    }

    public boolean interactWithDirection(Direction dir) {
        Coordinate targetPos = currentLevel.getCurrentPosition().moveTo(dir);
        Room adjacentRoom = currentLevel.getRoomAt(targetPos);

        if (adjacentRoom == null) return false;
        if (adjacentRoom instanceof Lockable lockableRoom && lockableRoom.isLocked()) {
            if (player.getKeys() > 0) {
                player.consumeKey();
                lockableRoom.unlock();
                return true;
            }
            return false;
        }
        return currentLevel.movePlayer(dir);
    }

    public PlayerDTO getPlayerData() {
        if (player == null) throw new IllegalStateException("Player non inizializzato");

        return new PlayerDTO(
                player.getHeartsForDisplay(),
                player.getMaxHp() / 2.0,
                player.getGold(),
                player.getKeys()
        );
    }
}