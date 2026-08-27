package it.unicam.cs.mpgc.rpg122423.service.persistence;

import it.unicam.cs.mpgc.rpg122423.entity.ClearedRoomEntity;
import it.unicam.cs.mpgc.rpg122423.entity.PlayerEntity;
import it.unicam.cs.mpgc.rpg122423.entity.SaveGame;
import it.unicam.cs.mpgc.rpg122423.entity.SavedEnemyEntity;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Combattable;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Lootable;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.ShopRoom;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SaveService {

    private final HibernateUtil hibernateUtil;

    public SaveService(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    public void saveGame(DungeonService dungeonService, String lastEntryDirection) {
        Transaction transaction = null;
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            clearPreviousSaves(session);

            SaveGame saveGame = buildSaveGame(dungeonService, lastEntryDirection);
            saveClearedRooms(saveGame, dungeonService);
            saveActiveEnemies(saveGame, dungeonService);

            session.persist(saveGame);
            transaction.commit();
            System.out.println("Partita salvata con successo!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    private void clearPreviousSaves(Session session) {
        session.createQuery("DELETE FROM SavedEnemyEntity").executeUpdate();
        session.createQuery("DELETE FROM ClearedRoomEntity").executeUpdate();
        session.createQuery("DELETE FROM SaveGame").executeUpdate();
        session.createQuery("DELETE FROM PlayerEntity").executeUpdate();
    }

    private SaveGame buildSaveGame(DungeonService dungeonService, String lastEntryDirection) {
        Coordinate currentPos = dungeonService.getCurrentLevel().getCurrentPosition();

        SaveGame saveGame = new SaveGame();
        saveGame.setCurrentFloorNumber(dungeonService.getCurrentFloorNumber());
        saveGame.setCurrentX(currentPos.x());
        saveGame.setCurrentY(currentPos.y());
        saveGame.setSeed(dungeonService.getCurrentLevel().getFloor().getSeed());
        saveGame.setLastEntryDirection(lastEntryDirection);
        saveGame.setPlayer(buildPlayerEntity(dungeonService.getPlayer()));
        return saveGame;
    }

    private PlayerEntity buildPlayerEntity(Player player) {
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setCurrentHp(player.getCurrentHp());
        playerEntity.setMaxHp(player.getMaxHp());
        playerEntity.setGold(player.getGold());
        playerEntity.setKeys(player.getKeys());
        playerEntity.setBonusDamage(player.getBonusDamage());
        playerEntity.setCharacterType(player.getCharacterType().name());

        String diceElementsStr = player.getDicePool().getDiceList().stream()
                .map(d -> d.getElement().name())
                .collect(Collectors.joining(","));
        playerEntity.setDiceElements(diceElementsStr);

        return playerEntity;
    }

    private void saveClearedRooms(SaveGame saveGame, DungeonService dungeonService) {
        for (Map.Entry<Coordinate, Room> entry : dungeonService.getCurrentLevel().getFloor().getRooms().entrySet()) {
            Room r = entry.getValue();
            if (r.isCleared()) {
                boolean claimed = false;
                String shopData = null;
                if (r instanceof Lootable lootable) {
                    claimed = !lootable.hasLoot();
                }
                if (r instanceof ShopRoom shopRoom) {
                    shopData = shopRoom.getItemsForSale().stream()
                            .map(p -> String.valueOf(p.isBought()))
                            .collect(Collectors.joining(","));
                }
                saveGame.addClearedRoom(new ClearedRoomEntity(entry.getKey().x(), entry.getKey().y(), claimed, shopData));
            }
        }
    }

    private void saveActiveEnemies(SaveGame saveGame, DungeonService dungeonService) {
        Room currentRoom = dungeonService.getCurrentLevel().getCurrentRoom();
        if (currentRoom instanceof Combattable combattableRoom && !currentRoom.isCleared()) {
            for (Enemy enemy : combattableRoom.getEnemies()) {
                saveGame.addSavedEnemy(new SavedEnemyEntity(
                        enemy.getName(), enemy.getCurrentHp(), enemy.getMaxHp(), enemy.getBaseDamage()
                ));
            }
        }
    }

    public Optional<SaveGame> loadGame() {
        try (Session session = hibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM SaveGame", SaveGame.class)
                    .setMaxResults(1)
                    .uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
