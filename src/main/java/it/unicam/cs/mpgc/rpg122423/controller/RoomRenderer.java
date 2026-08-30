package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
import it.unicam.cs.mpgc.rpg122423.dto.EnemyDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.room.RoomType;

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
 * Gestisce esclusivamente il rendering visivo della stanza: pavimento, porte, player, nemici e loot.
 */
public class RoomRenderer {

    private static final double ROOM_WIDTH = 600;
    private static final double ROOM_HEIGHT = 400;
    private static final double DOOR_SIZE = 60;
    private static final double PLAYER_SIZE = 100;
    private static final double NORMAL_ENEMY_SIZE = 45;
    private static final double BOSS_ENEMY_SIZE = 80;
    private static final double TRAPDOOR_SIZE = 60;
    private static final double PADDING = 50;

    public static void renderFloor(Pane roomPane) {
        try {
            Image floorImg = new Image(RoomRenderer.class.getResourceAsStream("/assets/floor.png"));
            ImageView floorSprite = new ImageView(floorImg);
            floorSprite.setFitWidth(ROOM_WIDTH);
            floorSprite.setFitHeight(ROOM_HEIGHT);
            floorSprite.setSmooth(false);
            roomPane.getChildren().add(floorSprite);
        } catch (Exception e) {
            Rectangle fallback = new Rectangle(ROOM_WIDTH, ROOM_HEIGHT, Color.web("#6d4a3d"));
            roomPane.getChildren().add(fallback);
        }
    }

    public static void renderDoors(Pane roomPane, RoomDTO roomData, java.util.function.Consumer<Direction> onDoorClicked) {
        renderSingleDoor(roomPane, roomData.north(), Direction.NORTH, onDoorClicked);
        renderSingleDoor(roomPane, roomData.south(), Direction.SOUTH, onDoorClicked);
        renderSingleDoor(roomPane, roomData.east(), Direction.EAST, onDoorClicked);
        renderSingleDoor(roomPane, roomData.west(), Direction.WEST, onDoorClicked);
    }

    private static void renderSingleDoor(Pane roomPane, DoorDTO doorInfo, Direction dir, java.util.function.Consumer<Direction> onDoorClicked) {
        if (!doorInfo.exists()) return;

        double doorSize = DOOR_SIZE;
        String imagePath = "/assets/floorDoor.png";

        switch (doorInfo.roomType()) {
            case BOSS -> imagePath = "/assets/boosRoom Opened.png";
            case TREASURE -> imagePath = doorInfo.isLocked() ? "/assets/treasure locked.png" : "/assets/treasure opened.png";
            case SHOP -> imagePath = doorInfo.isLocked() ? "/assets/shop locked.png" : "/assets/shop opened.png";
            default -> { /* NORMAL: usa floorDoor.png di default */ }
        }

        try {
            ImageView doorSprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream(imagePath)));
            doorSprite.setFitWidth(doorSize);
            doorSprite.setPreserveRatio(true);
            doorSprite.setSmooth(false);
            doorSprite.setCursor(javafx.scene.Cursor.HAND);
            doorSprite.setOnMouseClicked(e -> {
                if (onDoorClicked != null) {
                    onDoorClicked.accept(dir);
                }
            });

            switch (dir) {
                case NORTH -> { doorSprite.setX(270); doorSprite.setY(-5); }
                case SOUTH -> { doorSprite.setX(270); doorSprite.setY(360); doorSprite.setRotate(180); }
                case EAST -> { doorSprite.setX(555); doorSprite.setY(170); doorSprite.setRotate(90); }
                case WEST -> { doorSprite.setX(-15); doorSprite.setY(170); doorSprite.setRotate(270); }
            }
            roomPane.getChildren().add(doorSprite);
        } catch (Exception e) {
            System.err.println("Errore nel rendering della porta " + dir + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void renderPlayer(Pane roomPane, Direction lastEntryDirection, String spritePath) {
        double playerSize = PLAYER_SIZE;
        if (spritePath != null && (spritePath.contains("DexPlayer") || spritePath.contains("IntPlayer"))) {
            playerSize = PLAYER_SIZE * 0.5; // Scale down by 50% to match the visual size of the Knight
        }
        
        try {
            ImageView playerSprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream(spritePath)));
            playerSprite.setFitWidth(playerSize);
            playerSprite.setFitHeight(playerSize);
            playerSprite.setPreserveRatio(true);
            playerSprite.setSmooth(false);

            double spawnX = (ROOM_WIDTH / 2.0) - (playerSize / 2.0);
            double spawnY = (ROOM_HEIGHT / 2.0) - (playerSize / 2.0);
            double padding = PADDING;

            if (lastEntryDirection != null) {
                switch (lastEntryDirection) {
                    case NORTH -> spawnY = ROOM_HEIGHT - playerSize - padding + 20;
                    case SOUTH -> spawnY = padding - 20;
                    case EAST -> spawnX = padding - 20;
                    case WEST -> spawnX = ROOM_WIDTH - playerSize - padding + 20;
                }
            }
            playerSprite.setX(spawnX);
            playerSprite.setY(spawnY);
            roomPane.getChildren().add(playerSprite);
        } catch (Exception e) {
            System.err.println("Errore nel rendering del player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int calculateClosestEnemy(List<EnemyDTO> enemies, Direction lastEntryDirection) {
        if (enemies == null || enemies.isEmpty()) return -1;

        double playerSize = PLAYER_SIZE;
        double pX = (ROOM_WIDTH / 2.0);
        double pY = (ROOM_HEIGHT / 2.0);
        double padding = PADDING;

        if (lastEntryDirection != null) {
            switch (lastEntryDirection) {
                case NORTH -> pY = ROOM_HEIGHT - playerSize - padding + 20 + (playerSize / 2.0);
                case SOUTH -> pY = padding - 20 + (playerSize / 2.0);
                case EAST -> pX = padding - 20 + (playerSize / 2.0);
                case WEST -> pX = ROOM_WIDTH - playerSize - padding + 20 + (playerSize / 2.0);
            }
        }

        double enemySize = NORMAL_ENEMY_SIZE;
        double centerX = (ROOM_WIDTH / 2.0) - (enemySize / 2.0);
        double centerY = (ROOM_HEIGHT / 2.0) - (enemySize / 2.0);
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

    public static void renderEnemies(Pane roomPane, List<EnemyDTO> enemies, int selectedEnemyIndex, Consumer<Integer> onEnemySelected, boolean isBossRoom) {
        if (enemies == null || enemies.isEmpty()) return;

        double enemySize = isBossRoom ? BOSS_ENEMY_SIZE : NORMAL_ENEMY_SIZE;
        double centerX = (ROOM_WIDTH / 2.0) - (enemySize / 2.0);
        double centerY = (ROOM_HEIGHT / 2.0) - (enemySize / 2.0);
        double[][] positions = { {centerX, centerY}, {centerX - 120, centerY - 80}, {centerX + 120, centerY - 80}, {centerX - 120, centerY + 80}, {centerX + 120, centerY + 80} };

        try {
            for (int i = 0; i < enemies.size(); i++) {
                EnemyDTO enemy = enemies.get(i);

                ImageView enemySprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream(enemy.spritePath())));
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
                } else if (enemy.isPoisoned()) {
                    DropShadow glow = new DropShadow();
                    glow.setColor(Color.LIMEGREEN);
                    glow.setSpread(0.6);
                    glow.setRadius(15);
                    enemySprite.setEffect(glow);
                }

                String statusText = (enemy.isBurned() ? " 🔥" : "") + (enemy.isPoisoned() ? " ☠️" : "");
                Label hpText = new Label("HP: " + enemy.currentHp() + "/" + enemy.maxHp() + statusText);
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
        } catch (Exception e) {
            System.err.println("Errore nel rendering dei nemici: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Renderizza la botola al centro della stanza per avanzare di piano. */
    public static void renderTrapdoor(Pane roomPane, Runnable onClick) {
        try {
            ImageView trapdoor = new ImageView(new Image(RoomRenderer.class.getResourceAsStream("/assets/change floor trapdor.png")));
            trapdoor.setFitWidth(TRAPDOOR_SIZE);
            trapdoor.setPreserveRatio(true);
            trapdoor.setSmooth(false);
            trapdoor.setX((ROOM_WIDTH / 2.0) - (TRAPDOOR_SIZE / 2.0));
            trapdoor.setY((ROOM_HEIGHT / 2.0) - (TRAPDOOR_SIZE / 2.0));
            trapdoor.setCursor(Cursor.HAND);

            DropShadow glow = new DropShadow();
            glow.setColor(Color.ORANGE);
            glow.setSpread(0.5);
            glow.setRadius(15);
            trapdoor.setEffect(glow);

            Label hint = new Label("Scendi al piano successivo");
            hint.setStyle("-fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 3px;");
            hint.setLayoutX((ROOM_WIDTH / 2.0) - 80);
            hint.setLayoutY((ROOM_HEIGHT / 2.0) + 35);

            trapdoor.setOnMouseClicked(e -> onClick.run());

            roomPane.getChildren().addAll(trapdoor, hint);
        } catch (Exception e) {
            System.err.println("Errore nel rendering della botola: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void renderLoot(Pane roomPane, RoomDTO roomData, Runnable onLootClicked) {
        if (!roomData.hasLoot() || roomData.lootImagePath() == null) return;

        double pedestalSize = 40;
        double itemSize = 30;

        // Piedistallo (placeholder o grafica se esiste)
        Rectangle pedestal = new Rectangle(pedestalSize, pedestalSize / 2, Color.DARKGRAY);
        double spawnX = (ROOM_WIDTH / 2.0) - (pedestalSize / 2.0);
        double spawnY = (ROOM_HEIGHT / 2.0) - (pedestalSize / 2.0) + 10;
        
        // Offset if boss room to not overlap perfectly with trapdoor
        if (roomData.isBossRoom()) {
            spawnY -= 50; 
        }
        
        pedestal.setX(spawnX);
        pedestal.setY(spawnY);
        pedestal.setArcWidth(10);
        pedestal.setArcHeight(10);

        try {
            ImageView lootSprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream(roomData.lootImagePath())));
            lootSprite.setFitWidth(itemSize);
            lootSprite.setFitHeight(itemSize);
            lootSprite.setPreserveRatio(true);
            lootSprite.setSmooth(false);
            lootSprite.setX((ROOM_WIDTH / 2.0) - (itemSize / 2.0));
            lootSprite.setY(spawnY - 20); // Sopra il piedistallo

            // Effetto hover
            DropShadow hoverShadow = new DropShadow(15, Color.GOLD);
            Group lootGroup = new Group(pedestal, lootSprite);
            lootGroup.setCursor(Cursor.HAND);
            lootGroup.setOnMouseEntered(e -> lootSprite.setEffect(hoverShadow));
            lootGroup.setOnMouseExited(e -> lootSprite.setEffect(null));
            lootGroup.setOnMouseClicked(e -> onLootClicked.run());

            roomPane.getChildren().add(lootGroup);
        } catch (Exception e) {
            System.err.println("Errore nel rendering del loot: " + e.getMessage());
        }
    }

    public static void renderShopItems(Pane roomPane, RoomDTO roomData, Consumer<Integer> onBuyClicked) {
        if (roomData.shopItems() == null || roomData.shopItems().isEmpty()) return;

        double pedestalSize = 40;
        double itemSize = 30;

        int numItems = roomData.shopItems().size();
        // Centers the group of items horizontally
        double startX = (ROOM_WIDTH / 2.0) - (numItems * 100.0) / 2.0 + 30;
        double spawnY = (ROOM_HEIGHT / 2.0) - (pedestalSize / 2.0);

        for (int i = 0; i < numItems; i++) {
            it.unicam.cs.mpgc.rpg122423.dto.ShopItemDTO shopItem = roomData.shopItems().get(i);
            double currentX = startX + i * 100;

            Rectangle pedestal = new Rectangle(pedestalSize, pedestalSize / 2, Color.DARKGRAY);
            pedestal.setX(currentX);
            pedestal.setY(spawnY + 10);
            pedestal.setArcWidth(10);
            pedestal.setArcHeight(10);

            try {
                ImageView itemSprite = new ImageView(new Image(RoomRenderer.class.getResourceAsStream(shopItem.imagePath())));
                itemSprite.setFitWidth(itemSize);
                itemSprite.setFitHeight(itemSize);
                itemSprite.setPreserveRatio(true);
                itemSprite.setSmooth(false);
                itemSprite.setX(currentX + (pedestalSize / 2.0) - (itemSize / 2.0));
                itemSprite.setY(spawnY - 10);

                Label priceLabel = new Label(shopItem.price() + "¢");
                priceLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 2px;");
                priceLabel.setLayoutX(currentX + 5);
                priceLabel.setLayoutY(spawnY + 30);

                Group lootGroup = new Group(pedestal, itemSprite, priceLabel);
                lootGroup.setCursor(Cursor.HAND);
                
                DropShadow hoverShadow = new DropShadow(15, Color.GOLD);
                lootGroup.setOnMouseEntered(e -> itemSprite.setEffect(hoverShadow));
                lootGroup.setOnMouseExited(e -> itemSprite.setEffect(null));
                
                final int index = shopItem.index();
                lootGroup.setOnMouseClicked(e -> onBuyClicked.accept(index));

                roomPane.getChildren().add(lootGroup);
            } catch (Exception e) {
                System.err.println("Errore nel rendering shop item: " + e.getMessage());
            }
        }
    }
}