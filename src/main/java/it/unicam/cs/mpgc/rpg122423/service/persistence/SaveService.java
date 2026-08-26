package it.unicam.cs.mpgc.rpg122423.service.persistence;

import it.unicam.cs.mpgc.rpg122423.entity.ClearedRoomEntity;
import it.unicam.cs.mpgc.rpg122423.entity.PlayerEntity;
import it.unicam.cs.mpgc.rpg122423.entity.SaveGame;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Room;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Map;
import java.util.Optional;

public class SaveService {

    public void saveGame(DungeonService dungeonService, String lastEntryDirection) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Svuotiamo i salvataggi precedenti (supponiamo un solo slot di salvataggio per ora)
            session.createQuery("DELETE FROM SavedEnemyEntity").executeUpdate();
            session.createQuery("DELETE FROM ClearedRoomEntity").executeUpdate();
            session.createQuery("DELETE FROM SaveGame").executeUpdate();
            session.createQuery("DELETE FROM PlayerEntity").executeUpdate();

            SaveGame saveGame = new SaveGame();
            saveGame.setCurrentFloorNumber(dungeonService.getCurrentFloorNumber());
            saveGame.setCurrentX(dungeonService.getCurrentLevel().getCurrentPosition().x());
            saveGame.setCurrentY(dungeonService.getCurrentLevel().getCurrentPosition().y());
            saveGame.setSeed(dungeonService.getCurrentLevel().getFloor().getSeed());
            saveGame.setLastEntryDirection(lastEntryDirection);

            Player player = dungeonService.getPlayer();
            PlayerEntity playerEntity = new PlayerEntity();
            playerEntity.setCurrentHp(player.getCurrentHp());
            playerEntity.setMaxHp(player.getMaxHp());
            playerEntity.setGold(player.getGold());
            playerEntity.setKeys(player.getKeys());
            playerEntity.setBonusDamage(player.getBonusDamage());
            playerEntity.setCharacterType(player.getCharacterType().name());
            
            // Save dice elements
            String diceElementsStr = player.getDicePool().getDiceList().stream()
                    .map(d -> d.getElement().name())
                    .collect(java.util.stream.Collectors.joining(","));
            playerEntity.setDiceElements(diceElementsStr);
            
            saveGame.setPlayer(playerEntity);

            // Salviamo le stanze pulite
            for (Map.Entry<Coordinate, Room> entry : dungeonService.getCurrentLevel().getFloor().getRooms().entrySet()) {
                Room r = entry.getValue();
                if (r.isCleared()) {
                    boolean claimed = false;
                    String shopData = null;
                    if (r instanceof it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Lootable lootable) {
                        claimed = !lootable.hasLoot();
                    }
                    if (r instanceof it.unicam.cs.mpgc.rpg122423.model.dungeon.room.ShopRoom shopRoom) {
                        shopData = shopRoom.getItemsForSale().stream()
                                .map(p -> String.valueOf(p.isBought()))
                                .collect(java.util.stream.Collectors.joining(","));
                    }
                    saveGame.addClearedRoom(new ClearedRoomEntity(entry.getKey().x(), entry.getKey().y(), claimed, shopData));
                }
            }

            // Se siamo nel mezzo di una stanza non pulita, salviamo i nemici attuali
            Room currentRoom = dungeonService.getCurrentLevel().getCurrentRoom();
            if (currentRoom instanceof it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Combattable combattableRoom && !currentRoom.isCleared()) {
                for (it.unicam.cs.mpgc.rpg122423.model.combat.Enemy enemy : combattableRoom.getEnemies()) {
                    // AbstractEnemy ha baseDamage
                    int damage = 0;
                    if (enemy instanceof it.unicam.cs.mpgc.rpg122423.model.combat.AbstractEnemy ae) {
                        damage = ae.getBaseDamage();
                    }
                    saveGame.addSavedEnemy(new it.unicam.cs.mpgc.rpg122423.entity.SavedEnemyEntity(
                            enemy.getName(), enemy.getCurrentHp(), enemy.getMaxHp(), damage
                    ));
                }
            }

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

    public Optional<SaveGame> loadGame() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM SaveGame", SaveGame.class)
                    .setMaxResults(1)
                    .uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
