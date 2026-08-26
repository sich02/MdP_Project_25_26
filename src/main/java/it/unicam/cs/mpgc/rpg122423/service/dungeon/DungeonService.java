package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.*;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Floor;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import it.unicam.cs.mpgc.rpg122423.model.item.Item;
import it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService;

import java.util.List;

/**
 * Servizio principale del dungeon. Delega combattimento a CombatService,
 * mappatura DTO a RoomDTOMapper, e salvataggio a SaveService (SRP, DIP).
 */
public class DungeonService {
    private DungeonLevel currentLevel;
    private final FloorGenerator generator;
    private final CombatService combatService;
    private final RoomDTOMapper roomDTOMapper;
    private final SaveService saveService;
    private Player player;

    private int currentFloorNumber = 1;

    public DungeonService(SaveService saveService) {
        this.generator = new FloorGenerator();
        this.combatService = new CombatService();
        this.roomDTOMapper = new RoomDTOMapper();
        this.saveService = saveService;
    }

    public DungeonLevel getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public CombatService getCombatService() {
        return combatService;
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

        // Restore dice elements
        String savedElements = saveGame.getPlayer().getDiceElements();
        if (savedElements != null && !savedElements.isEmpty()) {
            String[] elements = savedElements.split(",");
            List<it.unicam.cs.mpgc.rpg122423.model.dice.Dice> diceList = this.player.getDicePool().getDiceList();
            for (int i = 0; i < Math.min(elements.length, diceList.size()); i++) {
                try {
                    diceList.get(i).setElement(it.unicam.cs.mpgc.rpg122423.model.dice.Element.valueOf(elements[i]));
                } catch (IllegalArgumentException e) {
                    // Ignore unknown elements
                }
            }
        }

        Floor floor = generator.generateFloorWithSeed(currentFloorNumber, saveGame.getSeed());

        // Svuota stanze completate
        for (it.unicam.cs.mpgc.rpg122423.entity.ClearedRoomEntity cr : saveGame.getClearedRooms()) {
            Coordinate coord = new Coordinate(cr.getX(), cr.getY());
            java.util.Optional<Room> room = floor.getRoomAt(coord);
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

        Coordinate currentPos = new Coordinate(saveGame.getCurrentX(), saveGame.getCurrentY());

        // Restore active enemies if saved in the middle of a room
        if (saveGame.getSavedEnemies() != null && !saveGame.getSavedEnemies().isEmpty()) {
            java.util.Optional<Room> currentRoom = floor.getRoomAt(currentPos);
            if (currentRoom.isPresent() && currentRoom.get() instanceof Combattable combattableRoom) {
                List<it.unicam.cs.mpgc.rpg122423.model.combat.Enemy> generatedEnemies = combattableRoom.getEnemies();
                List<it.unicam.cs.mpgc.rpg122423.entity.SavedEnemyEntity> savedEnemies = saveGame.getSavedEnemies();
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
        saveService.saveGame(this, null);
    }

    public int getCurrentFloorNumber() {
        return currentFloorNumber;
    }

    // --- Delegati a RoomDTOMapper ---

    public RoomDTO getCurrentRoomData() {
        if (currentLevel == null) throw new IllegalStateException("Livello non inizializzato");
        return roomDTOMapper.toRoomDTO(currentLevel);
    }

    // --- Navigazione ---

    public boolean interactWithDirection(Direction dir) {
        Room currentRoom = currentLevel.getCurrentRoom();

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

    // --- Loot ---

    public Item claimLootInCurrentRoom() {
        Room currentRoom = currentLevel.getCurrentRoom();
        if (currentRoom instanceof Lootable lootable && lootable.hasLoot()) {
            Item loot = lootable.getLoot();
            if (loot != null) {
                loot.onPickup(player);
                System.out.println("Hai raccolto: " + loot.getName());
            }
            lootable.claimLoot();
            return loot;
        }
        return null;
    }

    // --- Shop ---

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
                    System.out.println("Hai acquistato: " + purchasable.getItem().getName());
                    return true;
                } else if (player.getGold() < purchasable.getPrice()) {
                    System.out.println("Non hai abbastanza monete per acquistare: " + purchasable.getItem().getName());
                }
            }
        }
        return false;
    }

    // --- Player DTO ---

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

    // --- Delegati a CombatService ---

    public boolean getPlayerHasRolled() { return combatService.hasPlayerRolled(player); }
    public boolean getPlayerHasAttacked() { return combatService.hasPlayerAttacked(player); }
    public int getPlayerRerollsLeft() { return combatService.getPlayerRerollsLeft(player); }
    public List<Integer> getPlayerDiceValues() { return combatService.getPlayerDiceValues(player); }
    public List<it.unicam.cs.mpgc.rpg122423.model.dice.Element> getPlayerDiceElements() { return combatService.getPlayerDiceElements(player); }

    public void setPlayerDiceElement(int index, it.unicam.cs.mpgc.rpg122423.model.dice.Element element) {
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