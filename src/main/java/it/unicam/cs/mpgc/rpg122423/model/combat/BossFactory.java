package it.unicam.cs.mpgc.rpg122423.model.combat;

import java.util.Random;

/**
 * Factory per i Boss. Pool globale di 4 boss disponibili su tutti i piani.
 * Le stats scalano automaticamente in base al piano tramite BossEnemy.
 */
public class BossFactory {

    private static final Random random = new Random();

    /** Definizioni base dei boss (nome, baseHp, baseDamage). */
    private static final String[][] BOSS_POOL = {
            {"Conquest", "40", "5"},
            {"Dark One", "50", "6"},
            {"Famine", "35", "7"},
            {"Little Horn", "45", "5"}
    };

    /**
     * Crea un boss casuale dalla pool globale, con stats scalate al piano.
     *
     * @param floorNumber numero del piano corrente
     * @return un BossEnemy pronto per il combattimento
     */
    public static BossEnemy createBossForFloor(int floorNumber) {
        int index = random.nextInt(BOSS_POOL.length);
        String name = BOSS_POOL[index][0];
        int baseHp = Integer.parseInt(BOSS_POOL[index][1]);
        int baseDmg = Integer.parseInt(BOSS_POOL[index][2]);
        return new BossEnemy(name, baseHp, baseDmg, floorNumber);
    }
}
