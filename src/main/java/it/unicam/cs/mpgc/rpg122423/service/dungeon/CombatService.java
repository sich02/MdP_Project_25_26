package it.unicam.cs.mpgc.rpg122423.service.dungeon;

import it.unicam.cs.mpgc.rpg122423.dto.EnemyAction;
import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.Player;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;
import it.unicam.cs.mpgc.rpg122423.model.dice.Dice;
import it.unicam.cs.mpgc.rpg122423.model.dice.Element;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.Combattable;
import it.unicam.cs.mpgc.rpg122423.model.status.StatusEffect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gestisce tutta la logica di combattimento: roll dadi, reroll, attacco,
 * turno nemici, applicazione effetti elementali (SRP — estratto da DungeonService).
 */
public class CombatService {

    private static final double ELEMENTAL_PROC_CHANCE = 0.35;
    private static final double CHAIN_DAMAGE_MULTIPLIER = 0.5;

    // --- Gestione Dadi ---

    public boolean hasPlayerRolled(Player player) {
        return player != null && player.hasRolled();
    }

    public boolean hasPlayerAttacked(Player player) {
        return player != null && player.hasAttacked();
    }

    public int getPlayerRerollsLeft(Player player) {
        return player != null ? player.getRerollsLeft() : 0;
    }

    public List<Integer> getPlayerDiceValues(Player player) {
        if (player == null) return List.of(1, 1, 1, 1, 1);
        return player.getDicePool().getValues();
    }

    public List<Element> getPlayerDiceElements(Player player) {
        if (player == null) return List.of();
        return player.getDicePool().getDiceList().stream()
                .map(Dice::getElement)
                .toList();
    }

    public void setPlayerDiceElement(Player player, int index, Element element) {
        if (player != null && index >= 0 && index < player.getDicePool().getSize()) {
            player.getDicePool().getDiceList().get(index).setElement(element);
        }
    }

    public void rollPlayerDice(Player player) {
        if (player != null && !player.hasRolled()) {
            player.getDicePool().rollAll();
            player.setHasRolled(true);
        }
    }

    public void rerollPlayerDice(Player player, List<Integer> indices) {
        if (player != null && player.hasRolled() && player.getRerollsLeft() > 0) {
            player.getDicePool().rollSpecific(indices);
            player.decrementRerolls();
        }
    }

    // --- Attacco Giocatore ---

    public void executePlayerAttack(Player player, Combattable combattable, int damage, int targetIndex) {
        if (combattable.getCurrentPhase() == TurnPhase.ENEMY_TURN) return;

        List<Enemy> aliveEnemies = combattable.getEnemies().stream()
                .filter(e -> !e.isDead()).toList();

        if (targetIndex < 0 || targetIndex >= aliveEnemies.size()) return;

        Enemy target = aliveEnemies.get(targetIndex);
        target.takeDamage(damage);

        applyElementalEffects(player, target, aliveEnemies, targetIndex, damage);

        player.setHasAttacked(true);
        System.out.println("Hai inflitto " + damage + " danni a " + target.getName() + "!");
    }

    /**
     * Applica gli effetti elementali dei dadi del giocatore al bersaglio (OCP — usa Element.createStatusEffect()).
     */
    private void applyElementalEffects(Player player, Enemy target, List<Enemy> aliveEnemies, int targetIndex, int damage) {
        int electricProcs = 0;

        for (Dice d : player.getDicePool().getDiceList()) {
            Element element = d.getElement();
            if (element == Element.NONE) continue;

            if (element == Element.ELECTRIC) {
                // L'elettricità si attiva sempre (100% chance)
                electricProcs++;
            } else if (ThreadLocalRandom.current().nextDouble() < ELEMENTAL_PROC_CHANCE) {
                StatusEffect effect = element.createStatusEffect(target, d.getCurrentValue());
                if (effect != null) {
                    target.addStatusEffect(effect);
                    System.out.println(element.getDisplayName() + ": " + target.getName() + " subisce l'effetto! (Danno: " + d.getCurrentValue() + ")");
                }
            }
        }

        // Chain elettrica: colpisce sempre i bersagli aggiuntivi
        if (electricProcs > 0 && aliveEnemies.size() > 1) {
            int chainDamage = Math.max(1, (int) (damage * CHAIN_DAMAGE_MULTIPLIER));
            List<Enemy> closest = new ArrayList<>(aliveEnemies);
            closest.remove(target);
            closest.sort(Comparator.comparingInt(e -> Math.abs(aliveEnemies.indexOf(e) - targetIndex)));
            int chainTargets = Math.min(electricProcs, closest.size());
            for (int j = 0; j < chainTargets; j++) {
                closest.get(j).takeDamage(chainDamage);
                System.out.println("⚡ Elettricità a catena! " + closest.get(j).getName() + " subisce " + chainDamage + " danni.");
            }
        }
    }

    // --- Turno Nemici ---

    public void endPlayerTurn(Combattable combattable, Player player) {
        combattable.setPhase(TurnPhase.ENEMY_TURN);
        combattable.resetEnemyTurnIndex();
        System.out.println("Turno del giocatore terminato. Inizia il turno dei nemici.");
    }

    public String getNextAttackerName(Combattable combattable) {
        if (combattable.getCurrentPhase() != TurnPhase.ENEMY_TURN) return null;

        List<Enemy> aliveEnemies = combattable.getEnemies().stream()
                .filter(e -> !e.isDead()).toList();
        if (combattable.getCurrentEnemyTurnIndex() < aliveEnemies.size()) {
            return aliveEnemies.get(combattable.getCurrentEnemyTurnIndex()).getName();
        }
        return null;
    }

    public boolean executeNextEnemyTurn(Combattable combattable, Player player, boolean dodged) {
        if (combattable.getCurrentPhase() != TurnPhase.ENEMY_TURN) return false;

        List<Enemy> aliveEnemies = combattable.getEnemies().stream()
                .filter(e -> !e.isDead()).toList();

        if (combattable.getCurrentEnemyTurnIndex() < aliveEnemies.size()) {
            Enemy actingEnemy = aliveEnemies.get(combattable.getCurrentEnemyTurnIndex());
            EnemyAction action = actingEnemy.getNextAction();

            if (!dodged) {
                player.takeHit();
                System.out.println(actingEnemy.getName() + " ti colpisce e infligge " + action.damage() + " danni!");
            } else {
                System.out.println("SCHIVATA PERFETTA! " + actingEnemy.getName() + " ti ha mancato.");
            }

            actingEnemy.prepareNextAction();
            actingEnemy.tickStatusEffects();
            combattable.advanceEnemyTurnIndex();

            if (combattable.getCurrentEnemyTurnIndex() >= aliveEnemies.size()) {
                combattable.setPhase(TurnPhase.INITIAL_ROLL);
                combattable.resetEnemyTurnIndex();
                player.resetTurnState();
            }
            return true;
        }

        combattable.setPhase(TurnPhase.INITIAL_ROLL);
        combattable.resetEnemyTurnIndex();
        player.resetTurnState();
        return false;
    }
}
