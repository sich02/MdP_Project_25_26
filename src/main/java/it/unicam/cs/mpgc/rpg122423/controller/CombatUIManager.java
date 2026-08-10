package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.combat.ComboEvaluator;
import it.unicam.cs.mpgc.rpg122423.model.combat.ComboResult;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Random;

/**
 * Gestisce esclusivamente il rendering e la logica visiva del menù di combattimento (Dadi, Reroll, Combo).
 */
public class CombatUIManager {

    // Memoria di stato isolata dal resto del gioco
    private boolean hasPlayerRolled = false;
    private boolean hasPlayerAttacked = false;
    private int[] currentDiceRolls = {1, 1, 1, 1, 1};
    private int rerollsLeft = 3;
    private boolean isAnimating = false;

    public void resetState() {
        hasPlayerRolled = false;
        hasPlayerAttacked = false;
        currentDiceRolls = new int[]{1, 1, 1, 1, 1};
        rerollsLeft = 3;
        isAnimating = false;
    }

    public void render(Pane roomPane, RoomDTO roomData, DungeonService dungeonService, int selectedEnemyIndex, Runnable updateViewCallback, Runnable startEnemyTurnCallback) {
        if (roomData.enemies() == null || roomData.enemies().isEmpty()) return;

        Pane combatMenu = new Pane();
        combatMenu.setPickOnBounds(false);

        if ("ENEMY_TURN".equals(roomData.combatPhase())) {
            Label enemyTurnLabel = new Label("⌛ Turno dei Nemici in corso...");
            enemyTurnLabel.setStyle("-fx-text-fill: #ff4c4c; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 5px;");
            enemyTurnLabel.setLayoutX(160);
            enemyTurnLabel.setLayoutY(350);
            combatMenu.getChildren().add(enemyTurnLabel);
        } else {
            buildPlayerTurnUI(combatMenu, dungeonService, selectedEnemyIndex, updateViewCallback, startEnemyTurnCallback);
        }

        roomPane.getChildren().add(combatMenu);
    }

    private void buildPlayerTurnUI(Pane combatMenu, DungeonService dungeonService, int selectedEnemyIndex, Runnable updateViewCallback, Runnable startEnemyTurnCallback) {
        HBox mainDiceArea = new HBox(15);
        mainDiceArea.setAlignment(Pos.CENTER_LEFT);
        mainDiceArea.setLayoutX(90);
        mainDiceArea.setLayoutY(315);

        Label totalLabel = new Label("TOTALE:\n0");
        totalLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 8px; -fx-border-color: #4CAF50; -fx-border-width: 2px;");
        totalLabel.setAlignment(Pos.CENTER);

        VBox overlayAndDiceBox = new VBox(5);
        overlayAndDiceBox.setAlignment(Pos.CENTER);

        Label comboNameLabel = new Label("");
        comboNameLabel.setAlignment(Pos.CENTER);

        HBox diceBox = new HBox(8);
        ImageView[] diceViews = new ImageView[5];
        Label[] diceLabels = new Label[5];

        Button attackBtn = new Button("⚔ Attacca");

        for (int i = 0; i < 5; i++) {
            VBox singleDiceBox = new VBox(2);
            singleDiceBox.setAlignment(Pos.CENTER);

            ImageView diceSprite = new ImageView();
            diceSprite.setFitWidth(40);
            diceSprite.setFitHeight(40);
            diceSprite.setPreserveRatio(true);
            diceSprite.setSmooth(false);
            diceSprite.setImage(getDiceImage(currentDiceRolls[i]));
            diceViews[i] = diceSprite;

            Label diceDmgLabel = new Label(hasPlayerRolled ? "+" + currentDiceRolls[i] : "");
            diceDmgLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: rgba(0,0,0,0.4); -fx-padding: 2px;");
            diceLabels[i] = diceDmgLabel;

            final int finalI = i;
            diceSprite.setCursor(Cursor.HAND);
            diceSprite.setOnMouseClicked(e -> {
                if (hasPlayerRolled && !hasPlayerAttacked && rerollsLeft > 0 && !isAnimating) {
                    isAnimating = true;
                    rerollsLeft--;

                    comboNameLabel.setText("Reroll...");
                    comboNameLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");

                    playSingleDiceRollAnimation(finalI, diceViews[finalI], diceLabels[finalI], totalLabel, comboNameLabel, attackBtn, () -> {
                        isAnimating = false;
                        updateViewCallback.run();
                    });

                    updateViewCallback.run();
                }
            });

            singleDiceBox.getChildren().addAll(diceSprite, diceDmgLabel);
            diceBox.getChildren().add(singleDiceBox);
        }

        overlayAndDiceBox.getChildren().addAll(comboNameLabel, diceBox);
        mainDiceArea.getChildren().addAll(totalLabel, overlayAndDiceBox);

        if (hasPlayerRolled && !isAnimating) {
            updateComboUI(totalLabel, comboNameLabel, attackBtn);
        }

        VBox buttonsBox = new VBox(8);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setLayoutX(440);
        buttonsBox.setLayoutY(285);

        Label rerollInfo = new Label("Reroll: " + rerollsLeft);
        rerollInfo.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 3px;");

        String rollText = !hasPlayerRolled ? "🎲 Tira i Dadi" : "🔄 Reroll Disabilitato";
        Button rollBtn = new Button(rollText);
        Button endTurnBtn = new Button("⧖ Fine Turno");

        rollBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        attackBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        endTurnBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");

        rollBtn.setDisable(hasPlayerRolled || isAnimating);
        attackBtn.setDisable(!hasPlayerRolled || hasPlayerAttacked || isAnimating);
        endTurnBtn.setDisable(!hasPlayerAttacked || isAnimating);

        rollBtn.setOnAction(e -> {
            isAnimating = true;
            rollBtn.setDisable(true);
            attackBtn.setDisable(true);
            endTurnBtn.setDisable(true);

            playCascadingRollAnimation(0, diceViews, diceLabels, totalLabel, comboNameLabel, attackBtn, () -> {
                isAnimating = false;
                hasPlayerRolled = true;
                updateViewCallback.run();
            });
        });

        attackBtn.setOnAction(e -> {
            int finalDmg = ComboEvaluator.evaluate(currentDiceRolls).totalDamage();
            dungeonService.executePlayerAttack(finalDmg, selectedEnemyIndex);
            hasPlayerAttacked = true;
            updateViewCallback.run();
        });

        endTurnBtn.setOnAction(e -> {
            dungeonService.endPlayerTurn();
            updateViewCallback.run();
            startEnemyTurnCallback.run();
        });

        buttonsBox.getChildren().addAll(rerollInfo, rollBtn, attackBtn, endTurnBtn);
        combatMenu.getChildren().addAll(mainDiceArea, buttonsBox);
    }

    private void updateComboUI(Label totalLabel, Label comboLabel, Button attackBtn) {
        if (!hasPlayerRolled) {
            totalLabel.setText("TOTALE:\n0");
            comboLabel.setText("");
            attackBtn.setText("⚔ Attacca");
            return;
        }

        ComboResult result = ComboEvaluator.evaluate(currentDiceRolls);

        totalLabel.setText("TOTALE:\n" + result.totalDamage());
        comboLabel.setText(result.name());

        if (result.name().equals("NESSUNA COMBO") || result.name().equals("COPPIA")) {
            comboLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            comboLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 16px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #ffaa00, 5, 0.5, 0, 0);");
        }

        attackBtn.setText("⚔ Attacca (" + result.totalDamage() + " Danni)");
    }

    private void playCascadingRollAnimation(int index, ImageView[] diceViews, Label[] diceLabels, Label totalLabel, Label comboLabel, Button attackBtn, Runnable onComplete) {
        if (index >= 5) {
            updateComboUI(totalLabel, comboLabel, attackBtn);
            if (onComplete != null) onComplete.run();
            return;
        }

        Timeline timeline = new Timeline();
        Random rand = new Random();
        int frames = 6;

        for (int i = 0; i < frames; i++) {
            KeyFrame kf = new KeyFrame(Duration.millis(50 * i), e -> {
                diceViews[index].setImage(getDiceImage(rand.nextInt(6) + 1));
                diceLabels[index].setText("?");
            });
            timeline.getKeyFrames().add(kf);
        }

        KeyFrame finalKf = new KeyFrame(Duration.millis(50 * frames), e -> {
            currentDiceRolls[index] = rand.nextInt(6) + 1;
            diceViews[index].setImage(getDiceImage(currentDiceRolls[index]));
            diceLabels[index].setText("+" + currentDiceRolls[index]);

            int currentTotal = 0;
            for (int j = 0; j <= index; j++) currentTotal += currentDiceRolls[j];
            totalLabel.setText("TOTALE:\n" + currentTotal);
            comboLabel.setText("Calcolando...");
            comboLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px; -fx-font-style: italic;");

            playCascadingRollAnimation(index + 1, diceViews, diceLabels, totalLabel, comboLabel, attackBtn, onComplete);
        });

        timeline.getKeyFrames().add(finalKf);
        timeline.play();
    }

    private void playSingleDiceRollAnimation(int index, ImageView diceView, Label diceLabel, Label totalLabel, Label comboLabel, Button attackBtn, Runnable onComplete) {
        Timeline timeline = new Timeline();
        Random rand = new Random();
        int frames = 8;

        for (int i = 0; i < frames; i++) {
            KeyFrame kf = new KeyFrame(Duration.millis(50 * i), e -> {
                diceView.setImage(getDiceImage(rand.nextInt(6) + 1));
                diceLabel.setText("?");
            });
            timeline.getKeyFrames().add(kf);
        }

        KeyFrame finalKf = new KeyFrame(Duration.millis(50 * frames), e -> {
            currentDiceRolls[index] = rand.nextInt(6) + 1;
            diceView.setImage(getDiceImage(currentDiceRolls[index]));
            diceLabel.setText("+" + currentDiceRolls[index]);

            updateComboUI(totalLabel, comboLabel, attackBtn);

            if (onComplete != null) onComplete.run();
        });

        timeline.getKeyFrames().add(finalKf);
        timeline.play();
    }

    private Image getDiceImage(int value) {
        String name = switch (value) {
            case 1 -> "perspective-dice-six-faces-one.png";
            case 2 -> "perspective-dice-six-faces-two.png";
            case 3 -> "perspective-dice-six-faces-three.png";
            case 4 -> "perspective-dice-six-faces-four.png";
            case 5 -> "perspective-dice-six-faces-five.png";
            case 6 -> "perspective-dice-six-faces-six.png";
            default -> "perspective-dice-six-faces-one.png";
        };
        return new Image(getClass().getResourceAsStream("/assets/" + name));
    }
}