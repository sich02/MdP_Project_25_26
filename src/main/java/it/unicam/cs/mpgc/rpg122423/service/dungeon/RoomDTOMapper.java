package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.*;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.DungeonLevel;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import it.unicam.cs.mpgc.rpg122423.model.status.BurnEffect;
import it.unicam.cs.mpgc.rpg122423.model.status.PoisonEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsabile della conversione dal modello di dominio (Room, Enemy, ecc.)
 * ai DTO utilizzati dalla View.
 */
public class RoomDTOMapper {

    public RoomDTO toRoomDTO(DungeonLevel level) {
        Coordinate currentPos = level.getCurrentPosition();
        Room currentRoom = level.getCurrentRoom();

        // Aggiorna lo stato delle stanze di combattimento prima di costruire il DTO
        updateCombatRoomState(currentRoom);

        List<EnemyDTO> enemyDTOs = new ArrayList<>();
        String phase = "NONE";
        boolean isBossRoom = currentRoom instanceof BossRoom;
        boolean trapdoorActive = false;
        boolean hasLoot = false;
        String lootImagePath = null;
        String lootName = null;

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
                    boolean burned = enemy.getActiveEffects().stream()
                            .anyMatch(e -> BurnEffect.EFFECT_NAME.equals(e.getName()));
                    boolean poisoned = enemy.getActiveEffects().stream()
                            .anyMatch(e -> PoisonEffect.EFFECT_NAME.equals(e.getName()));
                    enemyDTOs.add(new EnemyDTO(
                            enemy.getName(),
                            enemy.getCurrentHp(),
                            enemy.getMaxHp(),
                            enemy.getNextAction() != null
                                    ? "Intento: " + enemy.getNextAction().damage() + " Danni"
                                    : "Intento: Sconosciuto",
                            burned,
                            poisoned
                    ));
                }
            }
            phase = combattable.getCurrentPhase().name();
        }

        if (currentRoom instanceof BossRoom br) {
            trapdoorActive = br.isTrapdoorActive();
        }

        List<ShopItemDTO> shopItemDTOs = mapShopItems(currentRoom);

        return new RoomDTO(
                inspectDoor(level, currentPos, Direction.NORTH),
                inspectDoor(level, currentPos, Direction.SOUTH),
                inspectDoor(level, currentPos, Direction.EAST),
                inspectDoor(level, currentPos, Direction.WEST),
                enemyDTOs,
                phase,
                isBossRoom,
                trapdoorActive,
                hasLoot,
                lootImagePath,
                lootName,
                shopItemDTOs
        );
    }

    /**
     * Aggiorna lo stato cleared delle stanze di combattimento.
     * Separato dalla query isCleared() per rispettare CQS (Fix LSP).
     */
    private void updateCombatRoomState(Room room) {
        if (room instanceof CombatRoom combatRoom) {
            combatRoom.checkAndClearIfAllDead();
        } else if (room instanceof BossRoom bossRoom) {
            bossRoom.checkAndClearIfBossDead();
        }
    }

    private List<ShopItemDTO> mapShopItems(Room currentRoom) {
        List<ShopItemDTO> shopItemDTOs = new ArrayList<>();
        if (currentRoom instanceof ShopRoom shopRoom) {
            List<ShopRoom.Purchasable> items = shopRoom.getItemsForSale();
            for (int i = 0; i < items.size(); i++) {
                ShopRoom.Purchasable purchasable = items.get(i);
                if (!purchasable.isBought()) {
                    shopItemDTOs.add(new ShopItemDTO(
                            i,
                            purchasable.getItem().getName(),
                            purchasable.getItem().getImagePath(),
                            purchasable.getPrice()
                    ));
                }
            }
        }
        return shopItemDTOs;
    }

    private DoorDTO inspectDoor(DungeonLevel level, Coordinate currentPos, Direction dir) {
        Coordinate targetPos = currentPos.moveTo(dir);
        Room adjacentRoom = level.getRoomAt(targetPos);

        if (adjacentRoom == null) {
            return new DoorDTO(false, RoomType.NORMAL, false);
        }

        RoomType type = adjacentRoom.getRoomType();
        boolean locked = false;

        if (adjacentRoom instanceof Lockable lockableRoom) {
            locked = lockableRoom.isLocked();
        }

        return new DoorDTO(true, type, locked);
    }
}
