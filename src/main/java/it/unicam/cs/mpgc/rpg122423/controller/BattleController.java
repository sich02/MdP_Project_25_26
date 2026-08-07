package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.model.combat.Enemy;
import it.unicam.cs.mpgc.rpg122423.model.combat.TurnPhase;
import it.unicam.cs.mpgc.rpg122423.service.combat.Battle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class BattleController {

    private Battle battle;
    private boolean dodgeWindowActive = false;
    private boolean playerDodged = false;

    @FXML private Label playerHpLabel;
    @FXML private Label enemyHpLabel;
    @FXML private Label phaseLabel;
    @FXML private Label rerollCountLabel;
    @FXML private Button rollButton;
    @FXML private Button rerollButton;
    @FXML private Button attackButton;

    public void initBattle(Battle battle) {
        this.battle = battle;
        updateView();
    }

    @FXML
    private void handleInitialRoll() {
        try {
            battle.performInitialRoll();
            updateView();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void handleReroll() {
        try {
            updateView();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (battle.getCurrentPhase() == TurnPhase.ENEMY_TURN && dodgeWindowActive) {
            if (event.getCode() == KeyCode.SPACE) {
                playerDodged = true;
                System.out.println("SCHIVATA RIUSCITA!");
            }
        }
    }

    private void updateView() {
        playerHpLabel.setText("HP Player: " + battle.getEnemies());
        phaseLabel.setText("Fase: " + battle.getCurrentPhase());
        rerollCountLabel.setText("Reroll disponibili: " + battle.getAvailableRerolls());

        boolean isInitial = battle.getCurrentPhase() == TurnPhase.INITIAL_ROLL;
        boolean isTactical = battle.getCurrentPhase() == TurnPhase.REROLL_OR_ATTACK;

        rollButton.setDisable(!isInitial);
        rerollButton.setDisable(!isTactical || battle.getAvailableRerolls() <= 0);
        attackButton.setDisable(!isTactical);
    }
}