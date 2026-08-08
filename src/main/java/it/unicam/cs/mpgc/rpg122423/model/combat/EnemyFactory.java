package it.unicam.cs.mpgc.rpg122423.model.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyFactory {
    private static final Random random = new Random();

    public static List<Enemy> generateEnemiesForFloor(int floorNumber) {
        int enemyCount = random.nextInt(5) + 1; // Genera numero da 1 a 5
        List<Enemy> enemies = new ArrayList<>();

        for (int i = 0; i < enemyCount; i++) {
            enemies.add(getRandomEnemyFromPool(floorNumber));
        }
        return enemies;
    }

    /**
     * Il vero e proprio "Pool". Qui puoi definire i nemici per ogni piano.
     * Essendo chiamato nel for loop, permette la generazione di doppioni!
     */
    private static Enemy getRandomEnemyFromPool(int floorNumber) {
        if (floorNumber == 1) {
            int roll = random.nextInt(3);
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
}