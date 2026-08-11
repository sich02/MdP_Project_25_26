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

    public boolean movePlayer(Direction dir) {
        if (currentLevel == null) return false;
        return currentLevel.movePlayer(dir);
    }

    public RoomDTO getCurrentRoomData() {
        if (currentLevel == null) throw new IllegalStateException("Livello non inizializzato");
        Coordinate currentPos = currentLevel.getCurrentPosition();
        Room currentRoom = currentLevel.getCurrentRoom();

        List<EnemyDTO> enemyDTOs = new ArrayList<>();
        String phase = "NONE";
        boolean isBossRoom = false;
        boolean trapdoorActive = false;

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
            if (!cr.isCleared()) {
                phase = cr.getCurrentPhase().name();
            }
        } else if (currentRoom instanceof BossRoom br) {
            isBossRoom = true;
            for (var enemy : br.getEnemies()) {
                if (!enemy.isDead()) {
                    enemyDTOs.add(new EnemyDTO(
                            enemy.getName(),
                            enemy.getCurrentHp(),
                            enemy.getMaxHp(),
                            enemy.getNextAction().description()
                    ));
                }
            }
            if (!br.isCleared()) {
                phase = br.getCurrentPhase().name();
            }
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

        // Blocco porte per CombatRoom non cleared
        if (currentRoom instanceof CombatRoom cr) {
            if (!cr.isCleared()) {
                System.out.println("Le porte sono bloccate! Devi sconfiggere l'orda prima di poter proseguire!");
                return false;
            }
        }

        // Blocco porte per BossRoom non cleared
        if (currentRoom instanceof BossRoom br) {
            if (!br.isCleared()) {
                System.out.println("Le porte sono bloccate! Devi sconfiggere il Boss prima di poter proseguire!");
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

    public void endPlayerTurn() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof CombatRoom cr && !cr.isCleared()) {
            cr.setPhase(TurnPhase.ENEMY_TURN);
            cr.resetEnemyTurnIndex();
            System.out.println("Turno del giocatore terminato. Inizia il turno dei nemici.");
        } else if (currentRoom instanceof BossRoom br && !br.isCleared()) {
            br.setPhase(TurnPhase.ENEMY_TURN);
            br.resetEnemyTurnIndex();
            System.out.println("Turno del giocatore terminato. Inizia il turno del Boss.");
        }
    }

    public void executePlayerAttack(int damage, int targetIndex) {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (currentRoom instanceof CombatRoom cr && cr.getCurrentPhase() != TurnPhase.ENEMY_TURN && !cr.isCleared()) {
            List<it.unicam.cs.mpgc.rpg122423.model.combat.Enemy> aliveEnemies =
                    cr.getEnemies().stream().filter(e -> !e.isDead()).toList();

            if (targetIndex >= 0 && targetIndex < aliveEnemies.size()) {
                var target = aliveEnemies.get(targetIndex);
                target.takeDamage(damage);
                System.out.println("Hai inflitto " + damage + " danni a " + target.getName() + "!");
            }
        } else if (currentRoom instanceof BossRoom br && br.getCurrentPhase() != TurnPhase.ENEMY_TURN && !br.isCleared()) {
            // Il boss è sempre il target 0
            var boss = br.getBoss();
            if (!boss.isDead()) {
                boss.takeDamage(damage);
                System.out.println("Hai inflitto " + damage + " danni al Boss " + boss.getName() + "!");
            }
        }
    }

    // --- Ritorna il nome del nemico che sta per attaccare per il QTE ---
    public String getNextAttackerName() {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (currentRoom instanceof CombatRoom cr && cr.getCurrentPhase() == TurnPhase.ENEMY_TURN) {
            List<it.unicam.cs.mpgc.rpg122423.model.combat.Enemy> aliveEnemies =
                    cr.getEnemies().stream().filter(e -> !e.isDead()).toList();
            if (cr.getCurrentEnemyTurnIndex() < aliveEnemies.size()) {
                return aliveEnemies.get(cr.getCurrentEnemyTurnIndex()).getName();
            }
        } else if (currentRoom instanceof BossRoom br && br.getCurrentPhase() == TurnPhase.ENEMY_TURN) {
            if (!br.getBoss().isDead() && br.getCurrentEnemyTurnIndex() == 0) {
                return br.getBoss().getName();
            }
        }

        return null;
    }

    // --- Accetta in input l'esito dello Skill Check manuale ---
    public boolean executeNextEnemyTurn(boolean dodged) {
        Room currentRoom = currentLevel.getCurrentRoom();

        if (currentRoom instanceof CombatRoom cr && cr.getCurrentPhase() == TurnPhase.ENEMY_TURN) {
            return executeCombatRoomEnemyTurn(cr, dodged);
        } else if (currentRoom instanceof BossRoom br && br.getCurrentPhase() == TurnPhase.ENEMY_TURN) {
            return executeBossRoomEnemyTurn(br, dodged);
        }

        return false;
    }

    private boolean executeCombatRoomEnemyTurn(CombatRoom cr, boolean dodged) {
        List<it.unicam.cs.mpgc.rpg122423.model.combat.Enemy> aliveEnemies =
                cr.getEnemies().stream().filter(e -> !e.isDead()).toList();

        if (cr.getCurrentEnemyTurnIndex() < aliveEnemies.size()) {
            var actingEnemy = aliveEnemies.get(cr.getCurrentEnemyTurnIndex());
            var action = actingEnemy.getNextAction();

            if (!dodged) {
                player.takeDamage(action.damage());
                System.out.println(actingEnemy.getName() + " ti colpisce e infligge " + action.damage() + " danni!");
            } else {
                System.out.println("SCHIVATA PERFETTA! " + actingEnemy.getName() + " ti ha mancato.");
            }

            actingEnemy.prepareNextAction();
            cr.advanceEnemyTurnIndex();

            if (cr.getCurrentEnemyTurnIndex() >= aliveEnemies.size()) {
                cr.setPhase(TurnPhase.INITIAL_ROLL);
                cr.resetEnemyTurnIndex();
            }
            return true;
        }

        cr.setPhase(TurnPhase.INITIAL_ROLL);
        cr.resetEnemyTurnIndex();
        return false;
    }

    private boolean executeBossRoomEnemyTurn(BossRoom br, boolean dodged) {
        var boss = br.getBoss();

        if (!boss.isDead() && br.getCurrentEnemyTurnIndex() == 0) {
            var action = boss.getNextAction();

            if (!dodged) {
                player.takeDamage(action.damage());
                System.out.println("Il Boss " + boss.getName() + " ti colpisce e infligge " + action.damage() + " danni!");
            } else {
                System.out.println("SCHIVATA PERFETTA! Il Boss " + boss.getName() + " ti ha mancato.");
            }

            boss.prepareNextAction();
            br.advanceEnemyTurnIndex();

            // Il boss è un solo nemico, fine del suo turno
            br.setPhase(TurnPhase.INITIAL_ROLL);
            br.resetEnemyTurnIndex();
            return true;
        }

        br.setPhase(TurnPhase.INITIAL_ROLL);
        br.resetEnemyTurnIndex();
        return false;
    }
}