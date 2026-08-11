package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.*;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
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
        this.currentFloorNumber = 1;
        System.out.println("Generazione procedurale del Piano " + currentFloorNumber + " in corso...");
        this.player = new Player();
        Floor floor = generator.generateFloor(currentFloorNumber);
        this.currentLevel = new DungeonLevel(floor.getRooms(), floor.getStartingCoordinate());
        System.out.println("Piano generato e pronto all'esplorazione!");
    }

    /** Avanza al piano successivo. Genera un nuovo layout e resetta la posizione. */
    public void advanceFloor() {
        currentFloorNumber++;
        System.out.println("Avanzamento al Piano " + currentFloorNumber + "...");
        Floor floor = generator.generateFloor(currentFloorNumber);
        this.currentLevel = new DungeonLevel(floor.getRooms(), floor.getStartingCoordinate());
        System.out.println("Piano " + currentFloorNumber + " generato!");
    }

    public int getCurrentFloorNumber() {
        return currentFloorNumber;
    }

    public RoomDTO getCurrentRoomData() {
        if (currentLevel == null) throw new IllegalStateException("Livello non inizializzato");
        Coordinate currentPos = currentLevel.getCurrentPosition();
        Room currentRoom = currentLevel.getCurrentRoom();

        List<EnemyDTO> enemyDTOs = new ArrayList<>();
        String phase = "NONE";
        boolean isBossRoom = currentRoom instanceof BossRoom;
        boolean trapdoorActive = false;

        if (currentRoom instanceof Combattable combattable) {
            for (Enemy enemy : combattable.getEnemies()) {
                if (!enemy.isDead()) {
                    enemyDTOs.add(new EnemyDTO(
                            enemy.getName(),
                            enemy.getCurrentHp(),
                            enemy.getMaxHp(),
                            enemy.getNextAction().description()
                    ));
                }
            }
            if (!currentRoom.isCleared()) {
                phase = combattable.getCurrentPhase().name();
            }
        }

        if (currentRoom instanceof BossRoom br) {
            trapdoorActive = br.isTrapdoorActive();
        }

        return new RoomDTO(
                inspectDoor(currentPos, Direction.NORTH),
                inspectDoor(currentPos, Direction.SOUTH),
                inspectDoor(currentPos, Direction.EAST),
                inspectDoor(currentPos, Direction.WEST),
                enemyDTOs,
                phase,
                isBossRoom,
                trapdoorActive
        );
    }

    private DoorDTO inspectDoor(Coordinate currentPos, Direction dir) {
        Coordinate targetPos = currentPos.moveTo(dir);
        Room adjacentRoom = currentLevel.getRoomAt(targetPos);

        if (adjacentRoom == null) {
            return new DoorDTO(false, "NONE", false);
        }

        String type = adjacentRoom.getRoomType();
        boolean locked = false;

        if (adjacentRoom instanceof Lockable lockableRoom) {
            locked = lockableRoom.isLocked();
        }

        return new DoorDTO(true, type, locked);
    }

    public boolean interactWithDirection(Direction dir) {
        Room currentRoom = currentLevel.getCurrentRoom();

        // Blocco porte per stanze di combattimento non cleared
        if (currentRoom instanceof Combattable && !currentRoom.isCleared()) {
            System.out.println("Le porte sono bloccate! Devi sconfiggere i nemici prima di poter proseguire!");
            return false;
        }

        Coordinate targetPos = currentLevel.getCurrentPosition().moveTo(dir);
        Room adjacentRoom = currentLevel.getRoomAt(targetPos);

        if (adjacentRoom == null) return false;

        if (adjacentRoom instanceof Lockable lockableRoom && lockableRoom.isLocked()) {
            if (player.getKeys() > 0) {
                player.consumeKey();
                lockableRoom.unlock();
                if (currentLevel.movePlayer(dir)) {
                    player.resetTurnState();
                    return true;
                }
                return false;
            }
            System.out.println("Ti serve una chiave per aprire questa porta!");
            return false;
        }
        
        if (currentLevel.movePlayer(dir)) {
            player.resetTurnState();
            return true;
        }
        return false;
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

    public void endPlayerTurn() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Combattable combattable && !currentRoom.isCleared()) {
            combattable.setPhase(TurnPhase.ENEMY_TURN);
            combattable.resetEnemyTurnIndex();
            System.out.println("Turno del giocatore terminato. Inizia il turno dei nemici.");
        }
    }

    // --- Metodi per la gestione del turno di combattimento (spostati dalla View) ---
    public boolean getPlayerHasRolled() { return player != null && player.hasRolled(); }
    public boolean getPlayerHasAttacked() { return player != null && player.hasAttacked(); }
    public int getPlayerRerollsLeft() { return player != null ? player.getRerollsLeft() : 0; }
    
    public List<Integer> getPlayerDiceValues() {
        if (player == null) return List.of(1, 1, 1, 1, 1);
        return player.getDicePool().getValues();
    }
    
    public void rollPlayerDice() {
        if (player != null && !player.hasRolled()) {
            player.getDicePool().rollAll();
            player.setHasRolled(true);
        }
    }
    
    public void rerollPlayerDice(List<Integer> indices) {
        if (player != null && player.hasRolled() && player.getRerollsLeft() > 0) {
            player.getDicePool().rollSpecific(indices);
            player.decrementRerolls();
        }
    }

    public void executePlayerAttack(int damage, int targetIndex) {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (currentRoom instanceof Combattable combattable
                && combattable.getCurrentPhase() != TurnPhase.ENEMY_TURN
                && !currentRoom.isCleared()) {
            List<Enemy> aliveEnemies = combattable.getEnemies().stream()
                    .filter(e -> !e.isDead()).toList();

            if (targetIndex >= 0 && targetIndex < aliveEnemies.size()) {
                Enemy target = aliveEnemies.get(targetIndex);
                target.takeDamage(damage);
                player.setHasAttacked(true);
                System.out.println("Hai inflitto " + damage + " danni a " + target.getName() + "!");
            }
        }
    }

    /** Ritorna il nome del nemico che sta per attaccare per il QTE. */
    public String getNextAttackerName() {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (currentRoom instanceof Combattable combattable
                && combattable.getCurrentPhase() == TurnPhase.ENEMY_TURN) {
            List<Enemy> aliveEnemies = combattable.getEnemies().stream()
                    .filter(e -> !e.isDead()).toList();
            if (combattable.getCurrentEnemyTurnIndex() < aliveEnemies.size()) {
                return aliveEnemies.get(combattable.getCurrentEnemyTurnIndex()).getName();
            }
        }

        return null;
    }

    /** Accetta in input l'esito dello Skill Check manuale. */
    public boolean executeNextEnemyTurn(boolean dodged) {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (currentRoom instanceof Combattable combattable
                && combattable.getCurrentPhase() == TurnPhase.ENEMY_TURN) {
            return executeEnemyTurnFor(combattable, dodged);
        }

        return false;
    }

    private boolean executeEnemyTurnFor(Combattable combattable, boolean dodged) {
        List<Enemy> aliveEnemies = combattable.getEnemies().stream()
                .filter(e -> !e.isDead()).toList();

        if (combattable.getCurrentEnemyTurnIndex() < aliveEnemies.size()) {
            Enemy actingEnemy = aliveEnemies.get(combattable.getCurrentEnemyTurnIndex());
            EnemyAction action = actingEnemy.getNextAction();

            if (!dodged) {
                player.takeHit();
                System.out.println(actingEnemy.getName() + " ti colpisce e infligge " + action.damage() + " danni!");
            } else {
                System.out.println("SCHIVATA PERFETTA! " + actingEnemy.getName() + " ti ha mancato.");
            }

            actingEnemy.prepareNextAction();
            combattable.advanceEnemyTurnIndex();

            if (combattable.getCurrentEnemyTurnIndex() >= aliveEnemies.size()) {
                combattable.setPhase(TurnPhase.INITIAL_ROLL);
                combattable.resetEnemyTurnIndex();
                player.resetTurnState();
            }
            return true;
        }

        combattable.setPhase(TurnPhase.INITIAL_ROLL);
        combattable.resetEnemyTurnIndex();
        player.resetTurnState();
        return false;
    }
}