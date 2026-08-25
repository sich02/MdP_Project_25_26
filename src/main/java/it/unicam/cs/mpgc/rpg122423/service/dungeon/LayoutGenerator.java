package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.model.dungeon.Coordinate;
import it.unicam.cs.mpgc.rpg122423.model.combat.BossEnemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.*;

import java.util.*;

public class LayoutGenerator {

    /** Pool globale dei boss: {nome, baseHp, baseDmg} */
    private static final String[][] BOSS_POOL = {
            {"Conquest", "40", "5"},
            {"Dark One", "50", "6"},
            {"Famine", "35", "7"},
            {"Little Horn", "45", "5"}
    };

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
        layout.put(bossRoomCoord, new BossRoom(createBoss(floorNumber)));

        if (treasureCoord != null) {
            boolean requiresKey = floorNumber >= 2;
            layout.put(treasureCoord, new TreasureRoom(requiresKey));
        }

        if (shopCoord != null) {
            boolean requiresKey = floorNumber >= 2;
            layout.put(shopCoord, new ShopRoom(requiresKey));
        }

        for (Coordinate coord : shape) {
            boolean generatesLoot = random.nextBoolean();
            it.unicam.cs.mpgc.rpg122423.model.item.Item lootItem = null;
            if (generatesLoot) {
                int roll = random.nextInt(5);
                lootItem = switch (roll) {
                    case 0 -> new it.unicam.cs.mpgc.rpg122423.model.item.CoinItem();
                    case 1 -> new it.unicam.cs.mpgc.rpg122423.model.item.KeyItem();
                    case 2 -> new it.unicam.cs.mpgc.rpg122423.model.item.HalfHeartItem();
                    case 3 -> new it.unicam.cs.mpgc.rpg122423.model.item.RedHeartItem();
                    default -> new it.unicam.cs.mpgc.rpg122423.model.item.DoubleHeartItem();
                };
            }
            layout.putIfAbsent(coord, new CombatRoom(generatesLoot, createEnemies(floorNumber), lootItem));
        }

        return layout;
    }

    /**
     * Crea una lista di 1-5 nemici standard dalla pool del piano corrente.
     */
    private List<Enemy> createEnemies(int floorNumber) {
        int enemyCount = random.nextInt(5) + 1;
        List<Enemy> result = new ArrayList<>();
        for (int i = 0; i < enemyCount; i++) {
            result.add(pickRandomEnemy(floorNumber));
        }
        return result;
    }

    private Enemy pickRandomEnemy(int floorNumber) {
        if (floorNumber == 1) {
            int roll = random.nextInt(6);
            return switch (roll) {
                case 0 -> new StandardEnemy("Black Bony", 15, 2);
                case 1 -> new StandardEnemy("Black Globin", 20, 3);
                case 2 -> new StandardEnemy("Black Knight", 25, 4);
                case 3 -> new StandardEnemy("Blood Cultist", 12, 2);
                case 4 -> new StandardEnemy("Coal Boy", 18, 3);
                default -> new StandardEnemy("Cultist", 10, 2);
            };
        } else {
            int roll = random.nextInt(2);
            return switch (roll) {
                case 0 -> new StandardEnemy("Orc", 30, 3);
                default -> new StandardEnemy("Goblin", 20, 2);
            };
        }
    }

    /**
     * Crea un boss casuale dalla pool globale con stats scalate al piano.
     */
    private BossEnemy createBoss(int floorNumber) {
        int index = random.nextInt(BOSS_POOL.length);
        String name = BOSS_POOL[index][0];
        int baseHp = Integer.parseInt(BOSS_POOL[index][1]);
        int baseDmg = Integer.parseInt(BOSS_POOL[index][2]);
        return new BossEnemy(name, baseHp, baseDmg, floorNumber);
    }
}