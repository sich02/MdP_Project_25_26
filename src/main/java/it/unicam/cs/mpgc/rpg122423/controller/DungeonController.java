package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
import it.unicam.cs.mpgc.rpg122423.dto.EnemyDTO;
import it.unicam.cs.mpgc.rpg122423.dto.PlayerDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DungeonController {

    @FXML private Label floorLabel;
    @FXML private Label hpLabel;
    @FXML private Label goldLabel;
    @FXML private Pane roomPane;
    @FXML private Label keysLabel;

    private ImageView playerSprite;
    private final DungeonService dungeonService = new DungeonService();

    private Direction lastEntryDirection = null;

    // --- MEMORIA DI STATO DEL COMBATTIMENTO ---
    private boolean hasPlayerRolled = false;
    private boolean hasPlayerAttacked = false;
    private int currentDiceRoll = 1;

    @FXML
    public void initialize() {
        System.out.println("Dungeon UI caricata. Avvio Service...");
        dungeonService.startNewRun();
        updateView();
    }

    private void updateView() {
        RoomDTO roomData = dungeonService.getCurrentRoomData();

        roomPane.getChildren().clear();
        renderFloor();

        renderDoor(roomData.north(), Direction.NORTH);
        renderDoor(roomData.south(), Direction.SOUTH);
        renderDoor(roomData.east(), Direction.EAST);
        renderDoor(roomData.west(), Direction.WEST);

        spawnEnemies(roomData.enemies());
        spawnPlayer();

        PlayerDTO playerStats = dungeonService.getPlayerData();
        hpLabel.setText("Cuori: " + playerStats.currentHearts() + " / " + playerStats.maxHearts());
        goldLabel.setText("Oro: " + playerStats.gold());
        keysLabel.setText("Chiavi: " + playerStats.keys());
        renderCombatUI(roomData);
    }

    private void renderFloor() {
        try {
            Image floorImg = new Image(getClass().getResourceAsStream("/assets/floor.png"));
            ImageView floorSprite = new ImageView(floorImg);

            floorSprite.setFitWidth(600);
            floorSprite.setFitHeight(400);
            floorSprite.setSmooth(false);

            roomPane.getChildren().add(floorSprite);
        } catch (Exception e) {
            System.out.println("Sprite pavimento non trovato: /assets/floor.png");
            Rectangle fallback = new Rectangle(600, 400, Color.web("#6d4a3d"));
            roomPane.getChildren().add(fallback);
        }
    }

    private void spawnEnemies(java.util.List<EnemyDTO> enemies) {
        if (enemies == null || enemies.isEmpty()) return;

        double enemySize = 45;

        double centerX = (600 / 2.0) - (enemySize / 2.0);
        double centerY = (400 / 2.0) - (enemySize / 2.0);

        double[][] positions = {
                {centerX, centerY},
                {centerX - 120, centerY - 80},
                {centerX + 120, centerY - 80},
                {centerX - 120, centerY + 80},
                {centerX + 120, centerY + 80}
        };

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

                Image img = new Image(getClass().getResourceAsStream("/assets/" + spriteName));
                ImageView enemySprite = new ImageView(img);

                enemySprite.setFitWidth(enemySize);
                enemySprite.setPreserveRatio(true);
                enemySprite.setSmooth(false);

                double enemyX = positions[i][0];
                double enemyY = positions[i][1];

                enemySprite.setX(enemyX);
                enemySprite.setY(enemyY);
                roomPane.getChildren().add(enemySprite);

                Label hpText = new Label("HP: " + enemy.currentHp() + "/" + enemy.maxHp());
                hpText.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
                hpText.setLayoutX(enemyX - 5);
                hpText.setLayoutY(enemyY - 15);

                Label intentText = new Label(enemy.intentDescription());
                intentText.setStyle("-fx-text-fill: #fcdb03; -fx-font-family: 'Courier New'; -fx-font-size: 10px;");
                intentText.setLayoutX(enemyX - 25);
                intentText.setLayoutY(enemyY - 30);

                roomPane.getChildren().addAll(hpText, intentText);
            }

        } catch (Exception e) {
            System.out.println("Impossibile caricare lo sprite del nemico");
        }
    }

    private void spawnPlayer() {
        double playerSize = 100;
        try {
            Image img = new Image(getClass().getResourceAsStream("/assets/player.png"));
            playerSprite = new ImageView(img);
            playerSprite.setFitWidth(playerSize);
            playerSprite.setFitHeight(playerSize);
            playerSprite.setPreserveRatio(true);
            playerSprite.setSmooth(false);

            double roomWidth = 600;
            double roomHeight = 400;
            double spawnX = (roomWidth / 2) - (playerSize / 2);
            double spawnY = (roomHeight / 2) - (playerSize / 2);
            double padding = 50;
            if (lastEntryDirection != null) {
                switch (lastEntryDirection) {
                    case NORTH -> spawnY = roomHeight - playerSize - padding + 20;
                    case SOUTH -> spawnY = padding - 20;
                    case EAST -> spawnX = padding - 20;
                    case WEST -> spawnX = roomWidth - playerSize - padding + 20;
                }
            }

            playerSprite.setX(spawnX);
            playerSprite.setY(spawnY);

            roomPane.getChildren().add(playerSprite);
        } catch (Exception e) {
            System.out.println("Impossibile caricare lo sprite, uso placeholder.");
            Rectangle placeholder = new Rectangle(playerSize, playerSize, Color.DEEPSKYBLUE);
            placeholder.setX((600 / 2) - (playerSize / 2));
            placeholder.setY((400 / 2) - (playerSize / 2));
            roomPane.getChildren().add(placeholder);
        }
    }

    private void renderDoor(DoorDTO doorInfo, Direction dir) {
        if (!doorInfo.exists()) return;
        double doorSize = 60;
        String imagePath = "/assets/floorDoor.png";

        switch (doorInfo.roomType()) {
            case "BOSS" -> imagePath = "/assets/boosRoom Opened.png";
            case "TREASURE" -> imagePath = doorInfo.isLocked() ?
                    "/assets/treasure locked.png" : "/assets/treasure opened.png";
            case "SHOP" -> imagePath = doorInfo.isLocked() ?
                    "/assets/shop locked.png" : "/assets/shop opened.png";
        }

        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            ImageView doorSprite = new ImageView(img);
            doorSprite.setFitWidth(doorSize);
            doorSprite.setPreserveRatio(true);
            doorSprite.setSmooth(false);

            double roomWidth = 600;
            double roomHeight = 400;
            double offsetNorth = -5;
            double offsetSouth = 20;
            double offsetEast = 15;
            double offsetWest = -15;

            switch (dir) {
                case NORTH -> {
                    doorSprite.setX((roomWidth / 2) - (doorSize / 2));
                    doorSprite.setY(offsetNorth);
                }
                case SOUTH -> {
                    doorSprite.setX((roomWidth / 2) - (doorSize / 2));
                    doorSprite.setY(roomHeight - doorSize + offsetSouth);
                    doorSprite.setRotate(180);
                }
                case EAST -> {
                    doorSprite.setX(roomWidth - doorSize + offsetEast);
                    doorSprite.setY((roomHeight / 2) - (doorSize / 2));
                    doorSprite.setRotate(90);
                }
                case WEST -> {
                    doorSprite.setX(offsetWest);
                    doorSprite.setY((roomHeight / 2) - (doorSize / 2));
                    doorSprite.setRotate(270);
                }
            }
            roomPane.getChildren().add(doorSprite);
        } catch (Exception e) {
            System.out.println("Errore caricamento sprite porta: " + imagePath);
        }
    }

    @FXML private void moveNorth(ActionEvent event) { tryMove(Direction.NORTH); }
    @FXML private void moveSouth(ActionEvent event) { tryMove(Direction.SOUTH); }
    @FXML private void moveEast(ActionEvent event) { tryMove(Direction.EAST); }
    @FXML private void moveWest(ActionEvent event) { tryMove(Direction.WEST); }

    private void tryMove(Direction dir) {
        if (dungeonService.interactWithDirection(dir)) {
            lastEntryDirection = dir;
            // AZZERA LA MEMORIA DEI TURNI QUANDO CAMBI STANZA
            hasPlayerRolled = false;
            hasPlayerAttacked = false;
            currentDiceRoll = 1;
            updateView();
        } else {
            System.out.println("Muro colpito, nemico vivo o chiave mancante!");
        }
    }

    private void renderCombatUI(RoomDTO roomData) {
        if (roomData.enemies() == null || roomData.enemies().isEmpty()) return;

        VBox combatMenu = new VBox(10);
        combatMenu.setLayoutX(20);
        combatMenu.setLayoutY(20);

        if ("ENEMY_TURN".equals(roomData.combatPhase())) {
            Button nextEnemyBtn = new Button("▶ Ricevi Attacchi dai Nemici");
            nextEnemyBtn.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white; -fx-font-weight: bold;");

            nextEnemyBtn.setOnAction(e -> {
                dungeonService.executeAllEnemyTurns();
                // IL TURNO TORNA AL PLAYER: RESETTIAMO LE SUE AZIONI
                hasPlayerRolled = false;
                hasPlayerAttacked = false;
                updateView();
            });

            combatMenu.getChildren().add(nextEnemyBtn);

        } else {
            HBox diceBox = new HBox(10);

            ImageView diceSprite = new ImageView();
            diceSprite.setFitWidth(40);
            diceSprite.setFitHeight(40);
            diceSprite.setPreserveRatio(true);
            diceSprite.setSmooth(false);

            // LEGGE DALLA MEMORIA QUALE DADO MOSTRARE
            String diceImageName = switch (currentDiceRoll) {
                case 1 -> "perspective-dice-six-faces-one.png";
                case 2 -> "perspective-dice-six-faces-two.png";
                case 3 -> "perspective-dice-six-faces-three.png";
                case 4 -> "perspective-dice-six-faces-four.png";
                case 5 -> "perspective-dice-six-faces-five.png";
                case 6 -> "perspective-dice-six-faces-six.png";
                default -> "perspective-dice-six-faces-one.png";
            };
            diceSprite.setImage(new Image(getClass().getResourceAsStream("/assets/" + diceImageName)));

            Button rollBtn = new Button("🎲 Tira il Dado");
            String attackText = hasPlayerRolled ? "⚔ Attacca (" + currentDiceRoll + " Danni)" : "⚔ Attacca";
            Button attackBtn = new Button(attackText);
            Button endTurnBtn = new Button("⧖ Fine Turno");

            rollBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
            attackBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

            // LA MAGIA DEI PULSANTI: LEGGE LA MEMORIA DI STATO
            rollBtn.setDisable(hasPlayerRolled);
            attackBtn.setDisable(!hasPlayerRolled || hasPlayerAttacked);
            endTurnBtn.setDisable(!hasPlayerAttacked);

            rollBtn.setOnAction(e -> {
                currentDiceRoll = new Random().nextInt(6) + 1;
                hasPlayerRolled = true;
                updateView();
            });

            attackBtn.setOnAction(e -> {
                dungeonService.executePlayerAttack(currentDiceRoll);
                hasPlayerAttacked = true;
                updateView();
            });

            endTurnBtn.setOnAction(e -> {
                dungeonService.endPlayerTurn();
                updateView();
            });

            diceBox.getChildren().addAll(diceSprite, rollBtn);
            combatMenu.getChildren().addAll(diceBox, attackBtn, endTurnBtn);
        }

        roomPane.getChildren().add(combatMenu);
    }
}