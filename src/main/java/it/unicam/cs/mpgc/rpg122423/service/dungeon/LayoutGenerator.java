package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.combat.BossEnemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;
import it.unicam.cs.mpgc.rpg122423.model.item.*;

import java.util.*;

/**
 * Genera il layout di un piano del dungeon, inclusi nemici e loot.
 * Ogni responsabilità di generazione è separata in metodi privati (SRP senza pattern creazionali).
 */
public class LayoutGenerator {

    // --- Costanti boss ---
    private static final String[][] BOSS_POOL = {
            {"Conquest", "40", "5"},
            {"Dark One", "50", "6"},
            {"Famine", "35", "7"},
            {"Little Horn", "45", "5"}
    };

    // --- Costanti combattimento ---
    private static final int MAX_ENEMIES_FLOOR_1 = 3;
    private static final int MAX_ENEMIES_DEFAULT = 5;

    // --- Costanti loot ---
    private static final int DROP_CHANCE = 75;
    private static final int COIN_WEIGHT = 50;  // 0-49 = moneta
    private static final int KEY_WEIGHT = 75;   // 50-74 = chiave, 75-99 = cuore

    // --- Costanti shop ---
    private static final int SHOP_HP_ITEM_CHANCE = 60;
    private static final int SHOP_DISCOUNT_CHANCE = 25;
    private static final int SHOP_MAIN_PRICE = 15;
    private static final int SHOP_CONSUMABLE_PRICE = 5;
    private static final int SHOP_DISCOUNT_PRICE = 3;

    private final ShapeGenerator shapeGenerator;
    private final TopologicalAnalyzer topologicalAnalyzer;
    private final Random random;

    public LayoutGenerator(Random random) {
        this.random = random;
        this.shapeGenerator = new ShapeGenerator(random);
        this.topologicalAnalyzer = new TopologicalAnalyzer();
    }

    public Map<Coordinate, Room> generateLayout(int floorNumber) {
        Set<Coordinate> shape = shapeGenerator.generateShape(floorNumber);
        Coordinate spawn = new Coordinate(0, 0);
        Map<Coordinate, Integer> distances = topologicalAnalyzer.calculateDistances(shape, spawn);
        List<Coordinate> deadEnds = topologicalAnalyzer.findDeadEnds(shape, spawn);

        Coordinate bossRoomCoord = spawn;
        int maxDist = -1;
        for (Map.Entry<Coordinate, Integer> entry : distances.entrySet()) {
            if (entry.getValue() > maxDist) {
                maxDist = entry.getValue();
                bossRoomCoord = entry.getKey();
            }
        }

        deadEnds.remove(bossRoomCoord);
        Collections.shuffle(deadEnds, random);
        Coordinate treasureCoord = !deadEnds.isEmpty() ? deadEnds.remove(0) : null;
        Coordinate shopCoord = !deadEnds.isEmpty() ? deadEnds.remove(0) : null;

        Map<Coordinate, Room> layout = new HashMap<>();
        layout.put(spawn, new SpawnRoom());
        layout.put(bossRoomCoord, new BossRoom(createBoss(floorNumber), ItemPool.getRandomItem()));

        if (treasureCoord != null) {
            layout.put(treasureCoord, new TreasureRoom(floorNumber >= 2, ItemPool.getRandomItem()));
        }
        if (shopCoord != null) {
            layout.put(shopCoord, new ShopRoom(floorNumber >= 2, createShopItems()));
        }

        for (Coordinate coord : shape) {
            Item lootItem = generateCombatLoot();
            layout.putIfAbsent(coord, new CombatRoom(lootItem != null, createEnemies(floorNumber), lootItem));
        }

        return layout;
    }

    // -------------------------------------------------------------------------
    // Metodi privati — creazione nemici
    // -------------------------------------------------------------------------

    private List<Enemy> createEnemies(int floorNumber) {
        int maxEnemies = (floorNumber == 1) ? MAX_ENEMIES_FLOOR_1 : MAX_ENEMIES_DEFAULT;
        int enemyCount = random.nextInt(maxEnemies) + 1;
        List<Enemy> result = new ArrayList<>();
        for (int i = 0; i < enemyCount; i++) {
            result.add(pickRandomEnemy(floorNumber));
        }
        return result;
    }

    private Enemy pickRandomEnemy(int floorNumber) {
        if (floorNumber == 1) {
            return switch (random.nextInt(6)) {
                case 0 -> new StandardEnemy("Black Bony", 15, 2);
                case 1 -> new StandardEnemy("Black Globin", 20, 3);
                case 2 -> new StandardEnemy("Black Knight", 25, 4);
                case 3 -> new StandardEnemy("Blood Cultist", 12, 2);
                case 4 -> new StandardEnemy("Coal Boy", 18, 3);
                default -> new StandardEnemy("Cultist", 10, 2);
            };
        } else {
            return switch (random.nextInt(2)) {
                case 0 -> new StandardEnemy("Orc", 30, 3);
                default -> new StandardEnemy("Goblin", 20, 2);
            };
        }
    }

    private BossEnemy createBoss(int floorNumber) {
        int index = random.nextInt(BOSS_POOL.length);
        String name = BOSS_POOL[index][0];
        int baseHp = Integer.parseInt(BOSS_POOL[index][1]);
        int baseDmg = Integer.parseInt(BOSS_POOL[index][2]);
        return new BossEnemy(name, baseHp, baseDmg, floorNumber);
    }

    // -------------------------------------------------------------------------
    // Metodi privati — generazione loot stanze combattimento
    // -------------------------------------------------------------------------

    /**
     * Genera un oggetto di loot per una stanza di combattimento con il 75% di probabilità.
     * @return l'oggetto generato, o null se la stanza non dropppa nulla
     */
    private Item generateCombatLoot() {
        if (random.nextInt(100) >= DROP_CHANCE) {
            return null;
        }
        int roll = random.nextInt(100);
        if (roll < COIN_WEIGHT) {
            return new CoinItem();
        } else if (roll < KEY_WEIGHT) {
            return new KeyItem();
        } else {
            return generateRandomHeart();
        }
    }

    private Item generateRandomHeart() {
        return switch (random.nextInt(3)) {
            case 0 -> new HalfHeartItem();
            case 1 -> new RedHeartItem();
            default -> new DoubleHeartItem();
        };
    }

    // -------------------------------------------------------------------------
    // Metodi privati — generazione oggetti shop
    // -------------------------------------------------------------------------

    private List<ShopRoom.Purchasable> createShopItems() {
        // Oggetto principale: 60% HP item, 40% item random
        Item mainItem = (random.nextInt(100) < SHOP_HP_ITEM_CHANCE)
                ? ItemPool.getRandomHpItem()
                : ItemPool.getRandomItem();

        // Consumabile: chiave o cuore, prezzo 5 (scontato a 3 col 25% di probabilità)
        Item consumable = random.nextBoolean() ? new KeyItem() : generateRandomHeart();
        int price = (random.nextInt(100) < SHOP_DISCOUNT_CHANCE) ? SHOP_DISCOUNT_PRICE : SHOP_CONSUMABLE_PRICE;

        return List.of(
                new ShopRoom.Purchasable(mainItem, SHOP_MAIN_PRICE),
                new ShopRoom.Purchasable(consumable, price)
        );
    }
}