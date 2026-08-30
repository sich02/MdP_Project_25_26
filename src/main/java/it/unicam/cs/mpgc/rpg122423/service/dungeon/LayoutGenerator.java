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
 * Ogni responsabilità di generazione è separata in metodi privati.
 */
public class LayoutGenerator {

    /** Dati di un boss: nome, HP base, danno base, percorso sprite. */
    private record BossData(String name, int baseHp, int baseDamage, String spritePath) {}

    private static final List<BossData> BOSS_POOL = List.of(
            new BossData("Conquest", 40, 5, "/assets/Boss_Conquest_Rebirth_ingame.png"),
            new BossData("Dark One", 50, 6, "/assets/Boss_Dark_One_Rebirth_ingame.png"),
            new BossData("Famine", 35, 7, "/assets/Boss_Famine_spitting_ingame.png"),
            new BossData("Little Horn", 45, 5, "/assets/Boss_Little_Horn_black_ingame.png")
    );

    // --- Costanti nemici standard per piano 1 ---
    private record EnemyData(String name, int hp, int damage, String spritePath) {}

    private static final List<EnemyData> FLOOR_1_ENEMIES = List.of(
            new EnemyData("Black Bony", 15, 2, "/assets/Black_Bony_Afterbirth.png"),
            new EnemyData("Black Globin", 20, 3, "/assets/Black_Globin.png"),
            new EnemyData("Black Knight", 25, 4, "/assets/Black_Knight.png"),
            new EnemyData("Blood Cultist", 12, 2, "/assets/Blood_Cultist.png"),
            new EnemyData("Coal Boy", 18, 3, "/assets/Coal_Boy.png"),
            new EnemyData("Cultist", 10, 2, "/assets/Cultist.png")
    );

    private static final List<EnemyData> FLOOR_OTHER_ENEMIES = List.of(
            new EnemyData("Orc", 30, 3, "/assets/Black_Bony_Afterbirth.png"),
            new EnemyData("Goblin", 20, 2, "/assets/Black_Bony_Afterbirth.png")
    );

    // --- Costanti combattimento ---
    private static final int MAX_ENEMIES_FLOOR_1 = 3;
    private static final int MAX_ENEMIES_DEFAULT = 5;

    // --- Costanti loot ---
    private static final int DROP_CHANCE = 75;
    private static final int COIN_WEIGHT = 50;
    private static final int KEY_WEIGHT = 75;

    // --- Costanti shop ---
    private static final int SHOP_HP_ITEM_CHANCE = 60;
    private static final int SHOP_DISCOUNT_CHANCE = 25;
    private static final int SHOP_MAIN_PRICE = 15;
    private static final int SHOP_CONSUMABLE_PRICE = 5;
    private static final int SHOP_DISCOUNT_PRICE = 3;

    private final ShapeGenerator shapeGenerator;
    private final TopologicalAnalyzer topologicalAnalyzer;
    private final ItemPool itemPool;
    private final Random random;

    public LayoutGenerator(Random random) {
        this.random = random;
        this.shapeGenerator = new ShapeGenerator(random);
        this.topologicalAnalyzer = new TopologicalAnalyzer();
        this.itemPool = new ItemPool();
    }

    public Map<Coordinate, Room> generateLayout(int floorNumber) {
        Set<Coordinate> shape = shapeGenerator.generateShape(floorNumber);
        Coordinate spawn = new Coordinate(0, 0);
        Map<Coordinate, Integer> distances = topologicalAnalyzer.calculateDistances(shape, spawn);
        List<Coordinate> deadEnds = topologicalAnalyzer.findDeadEnds(shape, spawn);

        Coordinate bossRoomCoord = findFarthestCoordinate(distances, spawn);

        deadEnds.remove(bossRoomCoord);
        Collections.shuffle(deadEnds, random);
        Coordinate treasureCoord = !deadEnds.isEmpty() ? deadEnds.remove(0) : null;
        Coordinate shopCoord = !deadEnds.isEmpty() ? deadEnds.remove(0) : null;

        Map<Coordinate, Room> layout = new HashMap<>();
        layout.put(spawn, new SpawnRoom());
        layout.put(bossRoomCoord, new BossRoom(createBoss(floorNumber), itemPool.getRandomItem(random)));

        if (treasureCoord != null) {
            layout.put(treasureCoord, new TreasureRoom(floorNumber >= 2, itemPool.getRandomItem(random)));
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

    private Coordinate findFarthestCoordinate(Map<Coordinate, Integer> distances, Coordinate fallback) {
        Coordinate farthest = fallback;
        int maxDist = -1;
        for (Map.Entry<Coordinate, Integer> entry : distances.entrySet()) {
            if (entry.getValue() > maxDist) {
                maxDist = entry.getValue();
                farthest = entry.getKey();
            }
        }
        return farthest;
    }

    // -------------------------------------------------------------------------
    // Creazione nemici
    // -------------------------------------------------------------------------

    private List<Enemy> createEnemies(int floorNumber) {
        int maxEnemies = (floorNumber == 1) ? MAX_ENEMIES_FLOOR_1 : MAX_ENEMIES_DEFAULT;
        int enemyCount = random.nextInt(maxEnemies) + 1;
        List<Enemy> result = new ArrayList<>();
        List<EnemyData> pool = (floorNumber == 1) ? FLOOR_1_ENEMIES : FLOOR_OTHER_ENEMIES;
        for (int i = 0; i < enemyCount; i++) {
            EnemyData data = pool.get(random.nextInt(pool.size()));
            result.add(new StandardEnemy(data.name(), data.hp(), data.damage(), data.spritePath()));
        }
        return result;
    }

    private BossEnemy createBoss(int floorNumber) {
        BossData data = BOSS_POOL.get(random.nextInt(BOSS_POOL.size()));
        return new BossEnemy(data.name(), data.baseHp(), data.baseDamage(), floorNumber, data.spritePath());
    }

    // -------------------------------------------------------------------------
    // Generazione loot stanze combattimento
    // -------------------------------------------------------------------------

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
        int roll = random.nextInt(3);
        if (roll == 0) return new HalfHeartItem();
        if (roll == 1) return new RedHeartItem();
        return new DoubleHeartItem();
    }

    // -------------------------------------------------------------------------
    // Generazione oggetti shop
    // -------------------------------------------------------------------------

    private List<ShopRoom.Purchasable> createShopItems() {
        Item mainItem = (random.nextInt(100) < SHOP_HP_ITEM_CHANCE)
                ? itemPool.getRandomHpItem(random)
                : itemPool.getRandomItem(random);

        Item consumable = random.nextBoolean() ? new KeyItem() : generateRandomHeart();
        int price = (random.nextInt(100) < SHOP_DISCOUNT_CHANCE) ? SHOP_DISCOUNT_PRICE : SHOP_CONSUMABLE_PRICE;

        return List.of(
                new ShopRoom.Purchasable(mainItem, SHOP_MAIN_PRICE),
                new ShopRoom.Purchasable(consumable, price)
        );
    }
}