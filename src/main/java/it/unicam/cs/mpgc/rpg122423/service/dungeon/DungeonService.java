package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.*;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;

import java.util.List;
import java.util.ArrayList;

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
        Room currentRoom = currentLevel.getCurrentRoom();

        List<EnemyDTO> enemyDTOs = new ArrayList<>();

        // Se la stanza è una CombatRoom, riempiamo la lista
        if (currentRoom instanceof CombatRoom cr) {
            for (var enemy : cr.getEnemies()) {
                if (!enemy.isDead()) {
                    enemyDTOs.add(new EnemyDTO(
                            enemy.getName(),
                            enemy.getCurrentHp(),
                            enemy.getMaxHp(),
                            enemy.getNextAction().description()
                    ));
                }
            }
        }

        String phase = "NONE";
        if (currentRoom instanceof CombatRoom cr && !cr.isCleared()) {
            phase = cr.getCurrentPhase().name();
        }

        return new RoomDTO(
                inspectDoor(currentPos, Direction.NORTH),
                inspectDoor(currentPos, Direction.SOUTH),
                inspectDoor(currentPos, Direction.EAST),
                inspectDoor(currentPos, Direction.WEST),
                enemyDTOs,
                phase
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
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof CombatRoom cr) {
            if (!cr.isCleared()) {
                System.out.println("Le porte sono bloccate! Devi sconfiggere l'orda prima di poter proseguire!");
                return false;
            }
        }

        Coordinate targetPos = currentLevel.getCurrentPosition().moveTo(dir);
        Room adjacentRoom = currentLevel.getRoomAt(targetPos);

        if (adjacentRoom == null) return false;

        if (adjacentRoom instanceof Lockable lockableRoom && lockableRoom.isLocked()) {
            if (player.getKeys() > 0) {
                player.consumeKey();
                lockableRoom.unlock();
                return true;
            }
            System.out.println("Ti serve una chiave per aprire questa porta!");
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

    // --- NUOVI METODI: GESTIONE TURNI COMBATTIMENTO ---

    public void endPlayerTurn() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof CombatRoom cr && !cr.isCleared()) {
            cr.setPhase(TurnPhase.ENEMY_TURN);
            cr.resetEnemyTurnIndex(); // Si riparte dal primo nemico
            System.out.println("Turno del giocatore terminato. Inizia il turno dei nemici.");
        }
    }

    public void executeAllEnemyTurns() {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (!(currentRoom instanceof CombatRoom cr) || cr.getCurrentPhase() != TurnPhase.ENEMY_TURN) {
            return;
        }

        // Filtriamo i nemici e li facciamo attaccare tutti in un solo colpo
        List<it.unicam.cs.mpgc.rpg122423.model.combat.Enemy> aliveEnemies =
                cr.getEnemies().stream()
                        .filter(e -> !e.isDead())
                        .toList();

        for (var enemy : aliveEnemies) {
            var action = enemy.getNextAction();
            player.takeDamage(action.damage());
            System.out.println(enemy.getName() + " ti attacca e infligge danno!");
            enemy.prepareNextAction();
        }

        // Finito il massacro, la palla torna al Player!
        cr.setPhase(TurnPhase.INITIAL_ROLL);
    }

    public void executePlayerAttack(int damage) {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof CombatRoom cr && cr.getCurrentPhase() != TurnPhase.ENEMY_TURN && !cr.isCleared()) {

            // Trova il primo bersaglio vivo
            var target = cr.getEnemies().stream().filter(e -> !e.isDead()).findFirst().orElse(null);

            if (target != null) {
                target.takeDamage(damage);
                System.out.println("Hai inflitto " + damage + " danni a " + target.getName() + "!");
            }
        }
    }
}