package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
import it.unicam.cs.mpgc.rpg122423.dto.EnemyDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

/**
 * Gestisce esclusivamente il rendering visivo della stanza: pavimento, porte, player e nemici.
 */
public class RoomRenderer {

    public static void renderFloor(Pane roomPane) {
        try {
            Image floorImg = new Image(RoomRenderer.class.getResourceAsStream("/assets/floor.png"));
            ImageView floorSprite = new ImageView(floorImg);
            floorSprite.setFitWidth(600);
            floorSprite.setFitHeight(400);
            floorSprite.setSmooth(false);
            roomPane.getChildren().add(floorSprite);
        } catch (Exception e) {
            Rectangle fallback = new Rectangle(600, 400, Color.web("#6d4a3d"));
            roomPane.getChildren().add(fallback);
        }
    }

    public static void renderDoors(Pane roomPane, RoomDTO roomData) {
        renderSingleDoor(roomPane, roomData.north(), Direction.NORTH);
        renderSingleDoor(roomPane, roomData.south(), Direction.SOUTH);
        renderSingleDoor(roomPane, roomData.east(), Direction.EAST);
        renderSingleDoor(roomPane, roomData.west(), Direction.WEST);
    }

    private static void renderSingleDoor(Pane roomPane, DoorDTO doorInfo, Direction dir) {
        if (!doorInfo.exists()) return;

        double doorSize = 60;
        String imagePath = "/assets/floorDoor.png";

        switch (doorInfo.roomType()) {
            case "BOSS" -> imagePath = "/assets/boosRoom Opened.png";
            case "TREASURE" -> imagePath = doorInfo.isLocked() ? "/assets/treasure locked.png" : "/assets/treasure opened.png";
            case "SHOP" -> imagePath = doorInfo.isLocked() ? "/assets/shop locked.png" : "/assets/shop opened.png";
        }

        try {
            ImageView doorSprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream(imagePath)));
            doorSprite.setFitWidth(doorSize);
            doorSprite.setPreserveRatio(true);
            doorSprite.setSmooth(false);

            switch (dir) {
                case NORTH -> { doorSprite.setX(270); doorSprite.setY(-5); }
                case SOUTH -> { doorSprite.setX(270); doorSprite.setY(360); doorSprite.setRotate(180); }
                case EAST -> { doorSprite.setX(555); doorSprite.setY(170); doorSprite.setRotate(90); }
                case WEST -> { doorSprite.setX(-15); doorSprite.setY(170); doorSprite.setRotate(270); }
            }
            roomPane.getChildren().add(doorSprite);
        } catch (Exception e) {}
    }

    public static void renderPlayer(Pane roomPane, Direction lastEntryDirection) {
        double playerSize = 100;
        try {
            ImageView playerSprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream("/assets/player.png")));
            playerSprite.setFitWidth(playerSize);
            playerSprite.setFitHeight(playerSize);
            playerSprite.setPreserveRatio(true);
            playerSprite.setSmooth(false);

            double spawnX = (600 / 2.0) - (playerSize / 2.0);
            double spawnY = (400 / 2.0) - (playerSize / 2.0);
            double padding = 50;

            if (lastEntryDirection != null) {
                switch (lastEntryDirection) {
                    case NORTH -> spawnY = 400 - playerSize - padding + 20;
                    case SOUTH -> spawnY = padding - 20;
                    case EAST -> spawnX = padding - 20;
                    case WEST -> spawnX = 600 - playerSize - padding + 20;
                }
            }
            playerSprite.setX(spawnX);
            playerSprite.setY(spawnY);
            roomPane.getChildren().add(playerSprite);
        } catch (Exception e) {}
    }

    public static int calculateClosestEnemy(List<EnemyDTO> enemies, Direction lastEntryDirection) {
        if (enemies == null || enemies.isEmpty()) return -1;

        double playerSize = 100;
        double pX = (600 / 2.0);
        double pY = (400 / 2.0);
        double padding = 50;

        if (lastEntryDirection != null) {
            switch (lastEntryDirection) {
                case NORTH -> pY = 400 - playerSize - padding + 20 + (playerSize / 2.0);
                case SOUTH -> pY = padding - 20 + (playerSize / 2.0);
                case EAST -> pX = padding - 20 + (playerSize / 2.0);
                case WEST -> pX = 600 - playerSize - padding + 20 + (playerSize / 2.0);
            }
        }

        double enemySize = 45;
        double centerX = (600 / 2.0) - (enemySize / 2.0);
        double centerY = (400 / 2.0) - (enemySize / 2.0);
        double[][] positions = { {centerX, centerY}, {centerX - 120, centerY - 80}, {centerX + 120, centerY - 80}, {centerX - 120, centerY + 80}, {centerX + 120, centerY + 80} };

        int closestIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < enemies.size(); i++) {
            double eX = positions[i][0] + (enemySize / 2.0);
            double eY = positions[i][1] + (enemySize / 2.0);
            double dist = Math.hypot(pX - eX, pY - eY);
            if (dist < minDistance) {
                minDistance = dist;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    public static void renderEnemies(Pane roomPane, List<EnemyDTO> enemies, int selectedEnemyIndex, Consumer<Integer> onEnemySelected) {
        if (enemies == null || enemies.isEmpty()) return;

        double enemySize = 45;
        double centerX = (600 / 2.0) - (enemySize / 2.0);
        double centerY = (400 / 2.0) - (enemySize / 2.0);
        double[][] positions = { {centerX, centerY}, {centerX - 120, centerY - 80}, {centerX + 120, centerY - 80}, {centerX - 120, centerY + 80}, {centerX + 120, centerY + 80} };

        try {
            for (int i = 0; i < enemies.size(); i++) {
                EnemyDTO enemy = enemies.get(i);
                String spriteName = switch (enemy.name()) {
                    case "Black Bony" -> "Black_Bony_Afterbirth.png";
                    case "Black Globin" -> "Black_Globin.png";
                    case "Black Knight" -> "Black_Knight.png";
                    case "Blood Cultist" -> "Blood_Cultist.png";
                    case "Coal Boy" -> "Coal_Boy.png";
                    case "Cultist" -> "Cultist.png";
                    default -> "Black_Bony_Afterbirth.png";
                };

                ImageView enemySprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream("/assets/" + spriteName)));
                enemySprite.setFitWidth(enemySize);
                enemySprite.setPreserveRatio(true);
                enemySprite.setSmooth(false);
                enemySprite.setX(positions[i][0]);
                enemySprite.setY(positions[i][1]);

                if (i == selectedEnemyIndex) {
                    DropShadow glow = new DropShadow();
                    glow.setColor(Color.RED);
                    glow.setSpread(0.6);
                    glow.setRadius(15);
                    enemySprite.setEffect(glow);
                }

                Label hpText = new Label("HP: " + enemy.currentHp() + "/" + enemy.maxHp());
                hpText.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
                hpText.setLayoutX(positions[i][0] - 5);
                hpText.setLayoutY(positions[i][1] - 15);

                Label intentText = new Label(enemy.intentDescription());
                intentText.setStyle("-fx-text-fill: #fcdb03; -fx-font-family: 'Courier New'; -fx-font-size: 10px;");
                intentText.setLayoutX(positions[i][0] - 25);
                intentText.setLayoutY(positions[i][1] - 30);

                Group enemyGroup = new Group(enemySprite, hpText, intentText);
                enemyGroup.setCursor(Cursor.HAND);

                final int finalI = i;
                enemyGroup.setOnMouseClicked(e -> onEnemySelected.accept(finalI));

                roomPane.getChildren().add(enemyGroup);
            }
        } catch (Exception e) {}
    }
}