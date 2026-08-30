package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.*;
import it.unicam.cs.mpgc.rpg122423.entity.ClearedRoomEntity;
import it.unicam.cs.mpgc.rpg122423.entity.SaveGame;
import it.unicam.cs.mpgc.rpg122423.entity.SavedEnemyEntity;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dice.Dice;
import it.unicam.cs.mpgc.rpg122423.model.dice.Element;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import it.unicam.cs.mpgc.rpg122423.model.item.Item;
import it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService;

import java.util.List;
import java.util.Optional;

/**
 * Servizio principale del dungeon. Delega combattimento a CombatService,
 * mappatura DTO a RoomDTOMapper, e salvataggio a SaveService.
 */
public class DungeonService {
    private DungeonLevel currentLevel;
    private final FloorGenerator generator;
    private final CombatService combatService;
    private final RoomDTOMapper roomDTOMapper;
    private final SaveService saveService;
    private Player player;

    private int currentFloorNumber = 1;
    private String loadedDirection = null;

    public DungeonService(SaveService saveService) {
        this.generator = new FloorGenerator();
        this.combatService = new CombatService();
        this.roomDTOMapper = new RoomDTOMapper();
        this.saveService = saveService;
    }

    public DungeonLevel getCurrentLevel() { return currentLevel; }
    public Player getPlayer() { return player; }
    public CombatService getCombatService() { return combatService; }
    public String getLoadedDirection() { return loadedDirection; }
    public int getCurrentFloorNumber() { return currentFloorNumber; }

    public void startNewRun(PlayableCharacter character) {
        this.currentFloorNumber = 1;
        this.player = new Player(character);
        Floor floor = generator.generateFloor(currentFloorNumber);
        this.currentLevel = new DungeonLevel(floor, floor.getStartingCoordinate());
    }

    // -------------------------------------------------------------------------
    // Restore — decomposto in helper (Fix SRP)
    // -------------------------------------------------------------------------

    public void restoreGame(SaveGame saveGame) {
        this.currentFloorNumber = saveGame.getCurrentFloorNumber();
        this.player = restorePlayer(saveGame);

        Floor floor = generator.generateFloorWithSeed(currentFloorNumber, saveGame.getSeed());
        restoreClearedRooms(floor, saveGame);

        Coordinate currentPos = new Coordinate(saveGame.getCurrentX(), saveGame.getCurrentY());
        restoreActiveEnemies(floor, currentPos, saveGame);

        this.currentLevel = new DungeonLevel(floor, currentPos);
        this.loadedDirection = saveGame.getLastEntryDirection();
    }

    private Player restorePlayer(SaveGame saveGame) {
        PlayableCharacter characterType = PlayableCharacter.KNIGHT;
        if (saveGame.getPlayer().getCharacterType() != null) {
            try {
                characterType = PlayableCharacter.valueOf(saveGame.getPlayer().getCharacterType());
            } catch (IllegalArgumentException e) {
                // Tipo personaggio sconosciuto, default a Cavaliere.
            }
        }

        Player restoredPlayer = new Player(characterType);
        restoredPlayer.restoreState(
                saveGame.getPlayer().getCurrentHp(),
                saveGame.getPlayer().getMaxHp(),
                saveGame.getPlayer().getGold(),
                saveGame.getPlayer().getKeys(),
                saveGame.getPlayer().getBonusDamage()
        );

        restoreDiceElements(restoredPlayer, saveGame.getPlayer().getDiceElements());
        return restoredPlayer;
    }

    private void restoreDiceElements(Player targetPlayer, String savedElements) {
        if (savedElements == null || savedElements.isEmpty()) return;
        String[] elements = savedElements.split(",");
        List<Dice> diceList = targetPlayer.getDicePool().getDiceList();
        for (int i = 0; i < Math.min(elements.length, diceList.size()); i++) {
            try {
                diceList.get(i).setElement(Element.valueOf(elements[i]));
            } catch (IllegalArgumentException e) {
                // Ignore unknown elements
            }
        }
    }

    private void restoreClearedRooms(Floor floor, SaveGame saveGame) {
        for (ClearedRoomEntity cr : saveGame.getClearedRooms()) {
            Coordinate coord = new Coordinate(cr.getX(), cr.getY());
            Optional<Room> room = floor.getRoomAt(coord);
            room.ifPresent(r -> {
                r.markAsCleared();
                if (cr.isLootClaimed() && r instanceof Lootable lootable) {
                    lootable.claimLoot();
                }
                if (cr.getShopBoughtData() != null && r instanceof ShopRoom shopRoom) {
                    String[] boughtFlags = cr.getShopBoughtData().split(",");
                    List<ShopRoom.Purchasable> items = shopRoom.getItemsForSale();
                    for (int i = 0; i < Math.min(boughtFlags.length, items.size()); i++) {
                        if (Boolean.parseBoolean(boughtFlags[i])) {
                            items.get(i).markAsBought();
                        }
                    }
                }
            });
        }
    }

    private void restoreActiveEnemies(Floor floor, Coordinate currentPos, SaveGame saveGame) {
        if (saveGame.getSavedEnemies() == null || saveGame.getSavedEnemies().isEmpty()) return;
        Optional<Room> currentRoom = floor.getRoomAt(currentPos);
        if (currentRoom.isPresent() && currentRoom.get() instanceof Combattable combattableRoom) {
            List<Enemy> generatedEnemies = combattableRoom.getEnemies();
            List<SavedEnemyEntity> savedEnemies = saveGame.getSavedEnemies();
            for (int i = 0; i < Math.min(generatedEnemies.size(), savedEnemies.size()); i++) {
                Enemy enemy = generatedEnemies.get(i);
                SavedEnemyEntity savedEnemy = savedEnemies.get(i);
                int damageToApply = enemy.getCurrentHp() - savedEnemy.getCurrentHp();
                if (damageToApply > 0) {
                    enemy.takeDamage(damageToApply);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Navigazione
    // -------------------------------------------------------------------------

    /** Avanza al piano successivo. Genera un nuovo layout e resetta la posizione. */
    public void advanceFloor() {
        currentFloorNumber++;
        Floor floor = generator.generateFloor(currentFloorNumber);
        this.currentLevel = new DungeonLevel(floor, floor.getStartingCoordinate());
    }

    public boolean interactWithDirection(Direction dir) {
        Room currentRoom = currentLevel.getCurrentRoom();

        // Aggiorna lo stato delle stanze combat prima del check
        if (currentRoom instanceof CombatRoom combatRoom) {
            combatRoom.checkAndClearIfAllDead();
        } else if (currentRoom instanceof BossRoom bossRoom) {
            bossRoom.checkAndClearIfBossDead();
        }

        if (currentRoom instanceof Combattable && !currentRoom.isCleared()) {
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
            return false;
        }

        if (currentLevel.movePlayer(dir)) {
            player.resetTurnState();
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Delegati a RoomDTOMapper
    // -------------------------------------------------------------------------

    public RoomDTO getCurrentRoomData() {
        if (currentLevel == null) throw new IllegalStateException("Livello non inizializzato");
        return roomDTOMapper.toRoomDTO(currentLevel);
    }

    // -------------------------------------------------------------------------
    // Loot
    // -------------------------------------------------------------------------

    public Item claimLootInCurrentRoom() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Lootable lootable && lootable.hasLoot()) {
            Item loot = lootable.getLoot();
            if (loot != null) {
                loot.onPickup(player);
            }
            lootable.claimLoot();
            return loot;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Shop
    // -------------------------------------------------------------------------

    public boolean buyShopItem(int index) {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof ShopRoom shopRoom) {
            List<ShopRoom.Purchasable> items = shopRoom.getItemsForSale();
            if (index >= 0 && index < items.size()) {
                ShopRoom.Purchasable purchasable = items.get(index);
                if (!purchasable.isBought() && player.getGold() >= purchasable.getPrice()) {
                    player.spendGold(purchasable.getPrice());
                    purchasable.getItem().onPickup(player);
                    purchasable.markAsBought();
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Player DTO
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Delegati a CombatService
    // -------------------------------------------------------------------------

    public boolean getPlayerHasRolled() { return combatService.hasPlayerRolled(player); }
    public boolean getPlayerHasAttacked() { return combatService.hasPlayerAttacked(player); }
    public int getPlayerRerollsLeft() { return combatService.getPlayerRerollsLeft(player); }
    public List<Integer> getPlayerDiceValues() { return combatService.getPlayerDiceValues(player); }
    public List<Element> getPlayerDiceElements() { return combatService.getPlayerDiceElements(player); }

    public void setPlayerDiceElement(int index, Element element) {
        combatService.setPlayerDiceElement(player, index, element);
    }

    public void rollPlayerDice() { combatService.rollPlayerDice(player); }
    public void rerollPlayerDice(List<Integer> indices) { combatService.rerollPlayerDice(player, indices); }

    public void executePlayerAttack(int damage, int targetIndex) {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Combattable combattable && !currentRoom.isCleared()) {
            combatService.executePlayerAttack(player, combattable, damage, targetIndex);
        }
    }

    public void endPlayerTurn() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Combattable combattable && !currentRoom.isCleared()) {
            combatService.endPlayerTurn(combattable, player);
        }
    }

    public String getNextAttackerName() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Combattable combattable) {
            return combatService.getNextAttackerName(combattable);
        }
        return null;
    }

    public boolean executeNextEnemyTurn(boolean dodged) {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Combattable combattable) {
            return combatService.executeNextEnemyTurn(combattable, player, dodged);
        }
        return false;
    }
}