package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.*;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;

import it.unicam.cs.mpgc.rpg122423.model.item.Item;

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

    public DungeonLevel getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public void startNewRun(it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter character) {
        this.currentFloorNumber = 1;
        System.out.println("Generazione procedurale del Piano " + currentFloorNumber + " in corso...");
        this.player = new Player(character);
        Floor floor = generator.generateFloor(currentFloorNumber);
        this.currentLevel = new DungeonLevel(floor, floor.getStartingCoordinate());
        System.out.println("Piano generato e pronto all'esplorazione!");
    }

    private String loadedDirection = null;

    public String getLoadedDirection() {
        return loadedDirection;
    }

    public void restoreGame(it.unicam.cs.mpgc.rpg122423.entity.SaveGame saveGame) {
        this.currentFloorNumber = saveGame.getCurrentFloorNumber();
        
        it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter characterType = it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter.KNIGHT;
        if (saveGame.getPlayer().getCharacterType() != null) {
            try {
                characterType = it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter.valueOf(saveGame.getPlayer().getCharacterType());
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo personaggio sconosciuto, default a Cavaliere.");
            }
        }
        
        this.player = new Player(characterType);
        this.player.restoreState(
                saveGame.getPlayer().getCurrentHp(),
                saveGame.getPlayer().getMaxHp(),
                saveGame.getPlayer().getGold(),
                saveGame.getPlayer().getKeys(),
                saveGame.getPlayer().getBonusDamage()
        );
        Floor floor = generator.generateFloorWithSeed(currentFloorNumber, saveGame.getSeed());
        
        // Svuota stanze completate
        for (it.unicam.cs.mpgc.rpg122423.entity.ClearedRoomEntity cr : saveGame.getClearedRooms()) {
            Coordinate coord = new Coordinate(cr.getX(), cr.getY());
            java.util.Optional<it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room> room = floor.getRoomAt(coord);
            room.ifPresent(it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room::markAsCleared);
        }

        Coordinate currentPos = new Coordinate(saveGame.getCurrentX(), saveGame.getCurrentY());
        
        // Restore active enemies if saved in the middle of a room
        if (saveGame.getSavedEnemies() != null && !saveGame.getSavedEnemies().isEmpty()) {
            java.util.Optional<it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room> currentRoom = floor.getRoomAt(currentPos);
            if (currentRoom.isPresent() && currentRoom.get() instanceof it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Combattable combattableRoom) {
                java.util.List<it.unicam.cs.mpgc.rpg122423.model.combat.Enemy> generatedEnemies = combattableRoom.getEnemies();
                java.util.List<it.unicam.cs.mpgc.rpg122423.entity.SavedEnemyEntity> savedEnemies = saveGame.getSavedEnemies();
                for (int i = 0; i < Math.min(generatedEnemies.size(), savedEnemies.size()); i++) {
                    it.unicam.cs.mpgc.rpg122423.model.combat.Enemy enemy = generatedEnemies.get(i);
                    it.unicam.cs.mpgc.rpg122423.entity.SavedEnemyEntity savedEnemy = savedEnemies.get(i);
                    int damageToApply = enemy.getCurrentHp() - savedEnemy.getCurrentHp();
                    if (damageToApply > 0) {
                        enemy.takeDamage(damageToApply);
                    }
                }
            }
        }

        this.currentLevel = new DungeonLevel(floor, currentPos);
        this.loadedDirection = saveGame.getLastEntryDirection();
        System.out.println("Partita caricata al Piano " + currentFloorNumber + "!");
    }

    /** Avanza al piano successivo. Genera un nuovo layout e resetta la posizione. */
    public void advanceFloor() {
        currentFloorNumber++;
        System.out.println("Avanzamento al Piano " + currentFloorNumber + "...");
        Floor floor = generator.generateFloor(currentFloorNumber);
        this.currentLevel = new DungeonLevel(floor, floor.getStartingCoordinate());
        System.out.println("Piano " + currentFloorNumber + " generato!");
        
        // Auto-save at floor change
        it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService saveService = new it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService();
        saveService.saveGame(this, null);
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
        boolean hasLoot = false;
        String lootImagePath = null;
        String lootName = null;

        // Forza l'aggiornamento dello stato della stanza (es. genera il loot se il boss è morto)
        boolean isRoomCleared = currentRoom.isCleared();

        if (currentRoom instanceof Lootable lootable) {
            hasLoot = lootable.hasLoot();
            if (hasLoot && lootable.getLoot() != null) {
                lootImagePath = lootable.getLoot().getImagePath();
                lootName = lootable.getLoot().getName();
            }
        }

        if (currentRoom instanceof Combattable combattable && !isRoomCleared) {
            for (Enemy enemy : combattable.getEnemies()) {
                if (!enemy.isDead()) {
                    boolean burned = enemy.getActiveEffects().stream().anyMatch(e -> e instanceof it.unicam.cs.mpgc.rpg122423.model.status.BurnEffect);
                    boolean poisoned = enemy.getActiveEffects().stream().anyMatch(e -> e instanceof it.unicam.cs.mpgc.rpg122423.model.status.PoisonEffect);
                    enemyDTOs.add(new EnemyDTO(
                            enemy.getName(),
                            enemy.getCurrentHp(),
                            enemy.getMaxHp(),
                            enemy.getNextAction() != null ? "Intento: " + enemy.getNextAction().damage() + " Danni" : "Intento: Sconosciuto",
                            burned,
                            poisoned
                    ));
                }
            }
            if (!isRoomCleared) {
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
                trapdoorActive,
                hasLoot,
                lootImagePath,
                lootName
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

    public it.unicam.cs.mpgc.rpg122423.model.item.Item claimLootInCurrentRoom() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Lootable lootable && lootable.hasLoot()) {
            it.unicam.cs.mpgc.rpg122423.model.item.Item loot = lootable.getLoot();
            if (loot != null) {
                loot.onPickup(player);
                System.out.println("Hai raccolto: " + loot.getName());
            }
            lootable.claimLoot();
            return loot;
        }
        return null;
    }

    public PlayerDTO getPlayerData() {
        if (player == null) throw new IllegalStateException("Player non inizializzato");

        return new PlayerDTO(
                player.getHeartsForDisplay(),
                player.getMaxHp() / 2.0,
                player.getGold(),
                player.getKeys(),
                player.getBonusDamage()
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
    
    public List<it.unicam.cs.mpgc.rpg122423.model.dice.Element> getPlayerDiceElements() {
        if (player == null) return List.of();
        return player.getDicePool().getDiceList().stream()
                .map(it.unicam.cs.mpgc.rpg122423.model.dice.Dice::getElement)
                .toList();
    }
    
    public void setPlayerDiceElement(int index, it.unicam.cs.mpgc.rpg122423.model.dice.Element element) {
        if (player != null && index >= 0 && index < player.getDicePool().getSize()) {
            player.getDicePool().getDiceList().get(index).setElement(element);
        }
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

                // Applica effetti elementali (Fuoco/Veleno/Elettro)
                int electricProcs = 0;
                for (it.unicam.cs.mpgc.rpg122423.model.dice.Dice d : player.getDicePool().getDiceList()) {
                    if (d.getElement() == it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE) {
                        if (java.util.concurrent.ThreadLocalRandom.current().nextDouble() < 0.35) {
                            target.addStatusEffect(new it.unicam.cs.mpgc.rpg122423.model.status.BurnEffect(target, d.getCurrentValue()));
                            System.out.println("🔥 " + target.getName() + " è stato bruciato! (Danno: " + d.getCurrentValue() + ")");
                        }
                    } else if (d.getElement() == it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON) {
                        if (java.util.concurrent.ThreadLocalRandom.current().nextDouble() < 0.35) {
                            target.addStatusEffect(new it.unicam.cs.mpgc.rpg122423.model.status.PoisonEffect(target, d.getCurrentValue()));
                            System.out.println("☠️ " + target.getName() + " è stato avvelenato! (Danno: " + d.getCurrentValue() + ")");
                        }
                    } else if (d.getElement() == it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC) {
                        if (java.util.concurrent.ThreadLocalRandom.current().nextDouble() < 0.35) {
                            electricProcs++;
                        }
                    }
                }

                // Ogni proc elettrico colpisce 1 mob adiacente diverso (no boss room)
                if (electricProcs > 0 && aliveEnemies.size() > 1) {
                    int chainDamage = Math.max(1, (int) (damage * 0.5));
                    java.util.List<Enemy> closest = new java.util.ArrayList<>(aliveEnemies);
                    closest.remove(target);
                    closest.sort(java.util.Comparator.comparingInt(e -> Math.abs(aliveEnemies.indexOf(e) - targetIndex)));
                    int chainTargets = Math.min(electricProcs, closest.size());
                    for (int j = 0; j < chainTargets; j++) {
                        closest.get(j).takeDamage(chainDamage);
                        System.out.println("⚡ Elettricità a catena! " + closest.get(j).getName() + " subisce " + chainDamage + " danni.");
                    }
                }

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
            actingEnemy.tickStatusEffects(); // Applica i danni nel tempo a fine turno (es. Bruciatura)
            
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