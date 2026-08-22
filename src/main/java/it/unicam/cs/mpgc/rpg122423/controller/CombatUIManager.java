package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.combat.ComboEvaluator;
import it.unicam.cs.mpgc.rpg122423.model.combat.ComboResult;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
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
 * Gestisce esclusivamente il rendering e la logica visiva del menù di
 * combattimento (Dadi, Reroll, Combo).
 */
public class CombatUIManager {

    // Stato puramente visivo
    private boolean isAnimating = false;

    public void render(Pane roomPane, RoomDTO roomData, DungeonService dungeonService, int selectedEnemyIndex,
            Runnable updateViewCallback, Runnable startEnemyTurnCallback) {
        if (roomData.enemies() == null || roomData.enemies().isEmpty())
            return;

        Pane combatMenu = new Pane();
        combatMenu.setPickOnBounds(false);

        if ("ENEMY_TURN".equals(roomData.combatPhase())) {
            Label enemyTurnLabel = new Label("⌛ Turno dei Nemici in corso...");
            enemyTurnLabel.setStyle(
                    "-fx-text-fill: #ff4c4c; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 5px;");
            enemyTurnLabel.setLayoutX(160);
            enemyTurnLabel.setLayoutY(420);
            combatMenu.getChildren().add(enemyTurnLabel);
        } else {
            buildPlayerTurnUI(combatMenu, dungeonService, selectedEnemyIndex, updateViewCallback,
                    startEnemyTurnCallback);
        }

        roomPane.getChildren().add(combatMenu);
    }

    private void buildPlayerTurnUI(Pane combatMenu, DungeonService dungeonService, int selectedEnemyIndex,
            Runnable updateViewCallback, Runnable startEnemyTurnCallback) {
        HBox mainDiceArea = new HBox(10);
        mainDiceArea.setAlignment(Pos.CENTER_LEFT);
        mainDiceArea.setLayoutX(90);
        mainDiceArea.setLayoutY(410);

        boolean hasPlayerRolled = dungeonService.getPlayerHasRolled();
        boolean hasPlayerAttacked = dungeonService.getPlayerHasAttacked();
        int rerollsLeft = dungeonService.getPlayerRerollsLeft();
        java.util.List<Integer> currentDiceRolls = dungeonService.getPlayerDiceValues();
        int bonusDamage = dungeonService.getPlayerData().bonusDamage();

        Label totalLabel = new Label("TOTALE:\n0");
        totalLabel.setStyle(
                "-fx-text-fill: #4CAF50; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 4px; -fx-border-color: #4CAF50; -fx-border-width: 1px;");
        totalLabel.setAlignment(Pos.CENTER);

        Label rerollInfo = new Label("Reroll: " + rerollsLeft);
        rerollInfo.setStyle(
                "-fx-text-fill: white; -fx-font-size: 11px; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 2px; -fx-text-alignment: center;");
        rerollInfo.setAlignment(Pos.CENTER);

        VBox totalAndRerollBox = new VBox(3);
        totalAndRerollBox.setAlignment(Pos.CENTER);
        totalAndRerollBox.getChildren().addAll(totalLabel, rerollInfo);

        VBox overlayAndDiceBox = new VBox(5);
        overlayAndDiceBox.setAlignment(Pos.CENTER);

        Label comboNameLabel = new Label("");
        comboNameLabel.setAlignment(Pos.CENTER);

        HBox diceBox = new HBox(8);
        ImageView[] diceViews = new ImageView[5];
        Label[] diceLabels = new Label[5];

        Button attackBtn = new Button("⚔ Attacca");
        java.util.List<it.unicam.cs.mpgc.rpg122423.model.dice.Element> diceElements = dungeonService.getPlayerDiceElements();

        for (int i = 0; i < 5; i++) {
            VBox singleDiceBox = new VBox(2);
            singleDiceBox.setAlignment(Pos.CENTER);

            ImageView diceSprite = new ImageView();
            diceSprite.setFitWidth(40);
            diceSprite.setFitHeight(40);
            diceSprite.setPreserveRatio(true);
            diceSprite.setSmooth(false);
            diceSprite.setImage(getDiceImage(currentDiceRolls.get(i)));
            
            if (diceElements != null && i < diceElements.size()) {
                if (diceElements.get(i) == it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE) {
                    javafx.scene.effect.DropShadow fireGlow = new javafx.scene.effect.DropShadow();
                    fireGlow.setColor(Color.ORANGERED);
                    fireGlow.setRadius(10);
                    fireGlow.setSpread(0.6);
                    diceSprite.setEffect(fireGlow);
                } else if (diceElements.get(i) == it.unicam.cs.mpgc.rpg122423.model.dice.Element.POISON) {
                    javafx.scene.effect.DropShadow poisonGlow = new javafx.scene.effect.DropShadow();
                    poisonGlow.setColor(Color.LIMEGREEN);
                    poisonGlow.setRadius(10);
                    poisonGlow.setSpread(0.6);
                    diceSprite.setEffect(poisonGlow);
                } else if (diceElements.get(i) == it.unicam.cs.mpgc.rpg122423.model.dice.Element.ELECTRIC) {
                    javafx.scene.effect.DropShadow electricGlow = new javafx.scene.effect.DropShadow();
                    electricGlow.setColor(Color.CYAN);
                    electricGlow.setRadius(10);
                    electricGlow.setSpread(0.6);
                    diceSprite.setEffect(electricGlow);
                }
            }
            
            diceViews[i] = diceSprite;

            Label diceDmgLabel = new Label(hasPlayerRolled ? "+" + currentDiceRolls.get(i) : "");
            diceDmgLabel.setStyle(
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: rgba(0,0,0,0.4); -fx-padding: 2px;");
            diceLabels[i] = diceDmgLabel;

            final int finalI = i;
            diceSprite.setCursor(Cursor.HAND);
            diceSprite.setOnMouseClicked(e -> {
                if (hasPlayerRolled && !hasPlayerAttacked && dungeonService.getPlayerRerollsLeft() > 0 && !isAnimating) {
                    isAnimating = true;
                    
                    // Effettua il roll reale nel model prima dell'animazione
                    dungeonService.rerollPlayerDice(java.util.List.of(finalI));
                    java.util.List<Integer> newValues = dungeonService.getPlayerDiceValues();

                    comboNameLabel.setText("Reroll...");
                    comboNameLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");

                    playSingleDiceRollAnimation(finalI, newValues, diceViews, diceLabels[finalI], totalLabel,
                            comboNameLabel, attackBtn, bonusDamage, () -> {
                                isAnimating = false;
                                updateViewCallback.run();
                            });
                }
            });

            singleDiceBox.getChildren().addAll(diceSprite, diceDmgLabel);
            diceBox.getChildren().add(singleDiceBox);
        }

        overlayAndDiceBox.getChildren().addAll(comboNameLabel, diceBox);
        mainDiceArea.getChildren().addAll(totalAndRerollBox, overlayAndDiceBox);

        if (hasPlayerRolled && !isAnimating) {
            updateComboUI(totalLabel, comboNameLabel, attackBtn, diceViews, currentDiceRolls, hasPlayerRolled, bonusDamage);
        }

        VBox buttonsBox = new VBox(5);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setLayoutX(440);
        buttonsBox.setLayoutY(412);

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

            // Effettua il roll reale nel model prima dell'animazione
            dungeonService.rollPlayerDice();
            java.util.List<Integer> targetValues = dungeonService.getPlayerDiceValues();

            playCascadingRollAnimation(0, targetValues, diceViews, diceLabels, totalLabel, comboNameLabel, attackBtn, bonusDamage, () -> {
                isAnimating = false;
                updateViewCallback.run();
            });
        });

        attackBtn.setOnAction(e -> {
            int baseDmg = ComboEvaluator.evaluate(currentDiceRolls.stream().mapToInt(i -> i).toArray()).totalDamage();
            int bonus = bonusDamage * currentDiceRolls.size();
            dungeonService.executePlayerAttack(baseDmg + bonus, selectedEnemyIndex);
            updateViewCallback.run();
        });

        endTurnBtn.setOnAction(e -> {
            dungeonService.endPlayerTurn();
            updateViewCallback.run();
            startEnemyTurnCallback.run();
        });

        buttonsBox.getChildren().addAll(rollBtn, attackBtn, endTurnBtn);
        combatMenu.getChildren().addAll(mainDiceArea, buttonsBox);
    }

    private void updateComboUI(Label totalLabel, Label comboLabel, Button attackBtn, ImageView[] diceViews, java.util.List<Integer> currentDiceRolls, boolean hasPlayerRolled, int bonusDamage) {
        if (!hasPlayerRolled || currentDiceRolls == null || currentDiceRolls.isEmpty()) {
            totalLabel.setText("TOTALE:\n0");
            comboLabel.setText("");
            attackBtn.setText("⚔ Attacca");
            clearDiceHighlights(diceViews);
            return;
        }

        ComboResult result = ComboEvaluator.evaluate(currentDiceRolls.stream().mapToInt(i -> i).toArray());
        int finalDmg = result.totalDamage() + (bonusDamage * currentDiceRolls.size());

        totalLabel.setText("TOTALE:\n" + finalDmg);
        comboLabel.setText(result.name());

        if (result.name().equals("Dado Più Alto") || result.name().equals("COPPIA")) {
            comboLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            comboLabel.setStyle(
                    "-fx-text-fill: #ffd700; -fx-font-size: 16px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, #ffaa00, 5, 0.5, 0, 0);");
        }

        highlightComboDice(diceViews, result);

        attackBtn.setText("⚔ Attacca (" + finalDmg + " Danni)");
    }

    /** Applica un effetto glow dorato ai dadi che fanno parte della combo. */
    private void highlightComboDice(ImageView[] diceViews, ComboResult result) {
        // Prima rimuovi tutti gli effetti
        clearDiceHighlights(diceViews);

        if (result.comboIndices().isEmpty()) return;

        DropShadow comboGlow = new DropShadow();
        comboGlow.setColor(Color.GOLD);
        comboGlow.setSpread(0.6);
        comboGlow.setRadius(12);

        for (int idx : result.comboIndices()) {
            if (idx >= 0 && idx < diceViews.length) {
                diceViews[idx].setEffect(comboGlow);
            }
        }
    }

    /** Rimuove gli effetti glow da tutti i dadi. */
    private void clearDiceHighlights(ImageView[] diceViews) {
        for (ImageView dv : diceViews) {
            dv.setEffect(null);
        }
    }

    private void playCascadingRollAnimation(int index, java.util.List<Integer> targetValues, ImageView[] diceViews, Label[] diceLabels, Label totalLabel,
            Label comboLabel, Button attackBtn, int bonusDamage, Runnable onComplete) {
        if (index >= 5) {
            updateComboUI(totalLabel, comboLabel, attackBtn, diceViews, targetValues, true, bonusDamage);
            if (onComplete != null)
                onComplete.run();
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
            int finalValue = targetValues.get(index);
            diceViews[index].setImage(getDiceImage(finalValue));
            diceLabels[index].setText("+" + finalValue);

            int currentTotal = 0;
            for (int j = 0; j <= index; j++)
                currentTotal += targetValues.get(j);
            totalLabel.setText("TOTALE:\n" + currentTotal);
            comboLabel.setText("Calcolando...");
            comboLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px; -fx-font-style: italic;");

            playCascadingRollAnimation(index + 1, targetValues, diceViews, diceLabels, totalLabel, comboLabel, attackBtn, bonusDamage, onComplete);
        });

        timeline.getKeyFrames().add(finalKf);
        timeline.play();
    }

    private void playSingleDiceRollAnimation(int index, java.util.List<Integer> targetValues, ImageView[] allDiceViews, Label diceLabel, Label totalLabel,
            Label comboLabel, Button attackBtn, int bonusDamage, Runnable onComplete) {
        ImageView diceView = allDiceViews[index];
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
            int finalValue = targetValues.get(index);
            diceView.setImage(getDiceImage(finalValue));
            diceLabel.setText("+" + finalValue);

            updateComboUI(totalLabel, comboLabel, attackBtn, allDiceViews, targetValues, true, bonusDamage);

            if (onComplete != null)
                onComplete.run();
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