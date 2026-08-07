package it.unicam.cs.mpgc.rpg122423.service.combat;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.StandardEnemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyProvider {
    private final Random random = new Random();

    public List<Enemy> provideEnemiesForRoom(int tier) {
        int enemyCount = 1 + random.nextInt(Math.min(tier, 3));
        List<Enemy> enemies = new ArrayList<>();

        for (int i = 0; i < enemyCount; i++) {
            String name = "Goblin " + (char) ('A' + i);
            int hp = 20 + (tier * 5);
            int damage = 5 + (tier * 2);

            enemies.add(new StandardEnemy(name, hp, damage));
        }

        return enemies;
    }
}