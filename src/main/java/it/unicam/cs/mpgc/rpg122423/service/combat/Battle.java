package it.unicam.cs.mpgc.rpg122423.service.combat;

import it.unicam.cs.mpgc.rpg122423.dto.Combo;
import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;

import java.util.ArrayList;
import java.util.List;

public class Battle {

    private final Player player;
    private final List<Enemy> enemies;

    private TurnPhase currentPhase;
    private int availableRerolls;
    private static final int MAX_REROLLS = 3;
    private int currentEnemyActingIndex;

    public Battle(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = new ArrayList<>(enemies);
        this.startPlayerTurn();
    }

    private void startPlayerTurn() {
        this.currentPhase = TurnPhase.INITIAL_ROLL;
        this.availableRerolls = MAX_REROLLS;
    }

    public void performInitialRoll() {
        if (currentPhase != TurnPhase.INITIAL_ROLL) {
            throw new IllegalStateException("Azione non consentita: il roll iniziale è già stato effettuato o non è il tuo turno.");
        }

        player.getDicePool().rollAll();
        currentPhase = TurnPhase.REROLL_OR_ATTACK;
    }

    public void performReroll(List<Integer> diceIndices) {
        if (currentPhase != TurnPhase.REROLL_OR_ATTACK) {
            throw new IllegalStateException("Azione non consentita: devi prima effettuare il roll iniziale.");
        }
        if (availableRerolls <= 0) {
            throw new IllegalStateException("Hai esaurito i reroll a disposizione per questo turno.");
        }

        player.getDicePool().rollSpecific(diceIndices);
        availableRerolls--;
    }

    public void playerAttack(Enemy target, Combo combo) {
        if (currentPhase != TurnPhase.REROLL_OR_ATTACK) {
            throw new IllegalStateException("Non puoi attaccare in questa fase del turno.");
        }
        if (!enemies.contains(target) || target.isDead()) {
            throw new IllegalArgumentException("Bersaglio non valido o già sconfitto.");
        }
        target.takeDamage(combo.totalDamage());

        checkWinCondition();

        if (currentPhase != TurnPhase.BATTLE_ENDED) {
            startEnemyTurn();
        }
    }

    private void startEnemyTurn() {
        this.currentPhase = TurnPhase.ENEMY_TURN;
        this.currentEnemyActingIndex = 0;
        advanceToNextAliveEnemy();
    }

    public void resolveEnemyAction(Enemy enemy, boolean dodged) {
        if (currentPhase != TurnPhase.ENEMY_TURN) {
            throw new IllegalStateException("Non è il turno dei nemici!");
        }

        Enemy expectedEnemy = enemies.get(currentEnemyActingIndex);
        if (!expectedEnemy.equals(enemy)) {
            throw new IllegalArgumentException("Ordine di turno violato. Ci si aspettava l'azione di un altro nemico.");
        }

        if (!dodged) {
            EnemyAction action = enemy.getNextAction();
            player.takeDamage(action.damage());
            // TODO: In futuro qui gestiremo anche gli StatusEffect contenuti nell'azione
        }

        checkLossCondition();

        if (currentPhase != TurnPhase.BATTLE_ENDED) {
            currentEnemyActingIndex++;
            advanceToNextAliveEnemy();
        }
    }

    private void advanceToNextAliveEnemy() {
        while (currentEnemyActingIndex < enemies.size() && enemies.get(currentEnemyActingIndex).isDead()) {
            currentEnemyActingIndex++;
        }

        if (currentEnemyActingIndex >= enemies.size()) {
            startPlayerTurn();
        }
    }

    private void checkWinCondition() {
        boolean allDead = enemies.stream().allMatch(Enemy::isDead);
        if (allDead) {
            currentPhase = TurnPhase.BATTLE_ENDED;
        }
    }

    private void checkLossCondition() {
        if (player.isDead()) {
            currentPhase = TurnPhase.BATTLE_ENDED;
        }
    }
    public TurnPhase getCurrentPhase() { return currentPhase; }
    public int getAvailableRerolls() { return availableRerolls; }
    public List<Enemy> getEnemies() { return enemies; }
}