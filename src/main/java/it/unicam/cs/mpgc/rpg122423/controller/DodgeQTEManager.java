package it.unicam.cs.mpgc.rpg122423.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Gestisce esclusivamente il minigioco visivo del Quick Time Event (QTE) per le schivate.
 */
public class DodgeQTEManager {

    public static void showDodgeQTE(Pane roomPane, String attackerName, Consumer<Boolean> onResult) {
        Pane qteContainer = new Pane();

        Rectangle dim = new Rectangle(600, 400, Color.rgb(0, 0, 0, 0.6));

        Pane barPane = new Pane();
        barPane.setLayoutX(150);
        barPane.setLayoutY(150);

        Label title = new Label(attackerName + " attacca!\nClicca lo schermo o premi SPAZIO per schivare!");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-alignment: center;");
        title.setPrefWidth(300);
        title.setAlignment(Pos.CENTER);
        title.setLayoutY(-50);

        Rectangle barBg = new Rectangle(300, 20, Color.DARKGRAY);
        barBg.setArcWidth(10); barBg.setArcHeight(10);
        barBg.setStroke(Color.WHITE);

        Random rand = new Random();
        double zoneWidth = 40 + rand.nextDouble() * 30;
        double zoneX = 120 + rand.nextDouble() * 120;

        Rectangle successZone = new Rectangle(zoneWidth, 20, Color.LIGHTGREEN);
        successZone.setX(zoneX);

        Rectangle cursor = new Rectangle(4, 30, Color.WHITE);
        cursor.setY(-5);
        cursor.setX(0);

        barPane.getChildren().addAll(title, barBg, successZone, cursor);
        qteContainer.getChildren().addAll(dim, barPane);

        // Aggiungiamo prima il contenitore alla scena
        roomPane.getChildren().add(qteContainer);

        Timeline qteTimeline = new Timeline();
        double speed = 4.0 + rand.nextDouble() * 3.0;

        final boolean[] resolved = {false};

        KeyFrame kf = new KeyFrame(Duration.millis(16), e -> {
            cursor.setX(cursor.getX() + speed);
            if (cursor.getX() > 300) {
                if (!resolved[0]) {
                    resolved[0] = true;
                    qteTimeline.stop();
                    showQTEResult(roomPane, barPane, false, qteContainer, onResult);
                }
            }
        });

        qteTimeline.getKeyFrames().add(kf);
        qteTimeline.setCycleCount(Timeline.INDEFINITE);

        Runnable handleAction = () -> {
            if (!resolved[0]) {
                resolved[0] = true;
                qteTimeline.stop();
                boolean success = cursor.getX() >= zoneX && cursor.getX() <= (zoneX + zoneWidth);
                showQTEResult(roomPane, barPane, success, qteContainer, onResult);
            }
        };
        dim.setOnMousePressed(e -> handleAction.run());

        qteContainer.setFocusTraversable(true);
        qteContainer.requestFocus();

        qteContainer.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                e.consume();
                handleAction.run();
            }
        });

        qteTimeline.play();
    }

    private static void showQTEResult(Pane roomPane, Pane barPane, boolean dodged, Pane qteContainer, Consumer<Boolean> onResult) {
        Label resultLabel = new Label(dodged ? "SCHIVATA PERFETTA!" : "COLPITO!");
        resultLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + (dodged ? "#00ff00" : "#ff0000") + "; -fx-effect: dropshadow(gaussian, black, 4, 1.0, 0, 0);");
        resultLabel.setLayoutX(dodged ? 20 : 90);
        resultLabel.setLayoutY(30);

        barPane.getChildren().add(resultLabel);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(e -> {
            roomPane.getChildren().remove(qteContainer);
            onResult.accept(dodged);
        });
        pause.play();
    }
}