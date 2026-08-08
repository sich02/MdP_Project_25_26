package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
import it.unicam.cs.mpgc.rpg122423.dto.EnemyDTO;
import it.unicam.cs.mpgc.rpg122423.dto.PlayerDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

    // Memoria per sapere da quale porta è appena entrato il giocatore
    private Direction lastEntryDirection = null;

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

        spawnEnemy(roomData.enemy());
        spawnPlayer();

        PlayerDTO playerStats = dungeonService.getPlayerData();
        hpLabel.setText("Cuori: " + playerStats.currentHearts() + " / " + playerStats.maxHearts());
        goldLabel.setText("Oro: " + playerStats.gold());
        keysLabel.setText("Chiavi: " + playerStats.keys());
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

    private void spawnEnemy(EnemyDTO enemy) {
        if (enemy == null) return;

        // Rimpicciolito per essere proporzionato al player!
        double enemySize = 45;

        try {
            Image img = new Image(getClass().getResourceAsStream("/assets/Black_Bony_Afterbirth.png"));
            ImageView enemySprite = new ImageView(img);
            enemySprite.setFitWidth(enemySize);
            enemySprite.setPreserveRatio(true);
            enemySprite.setSmooth(false);

            // Posizioniamolo al centro esatto (il player ora nasce sui bordi)
            double enemyX = (600 / 2) - (enemySize / 2);
            double enemyY = (400 / 2) - (enemySize / 2);

            enemySprite.setX(enemyX);
            enemySprite.setY(enemyY);

            roomPane.getChildren().add(enemySprite);

            // UI DEL NEMICO (riposizionata in base alle nuove dimensioni)
            Label hpText = new Label("HP: " + enemy.currentHp() + "/" + enemy.maxHp());
            hpText.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
            hpText.setLayoutX(enemyX - 5);
            hpText.setLayoutY(enemyY - 15);

            Label intentText = new Label(enemy.intentDescription());
            intentText.setStyle("-fx-text-fill: #fcdb03; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
            intentText.setLayoutX(enemyX - 25);
            intentText.setLayoutY(enemyY - 30);

            roomPane.getChildren().addAll(hpText, intentText);

        } catch (Exception e) {
            System.out.println("Impossibile caricare lo sprite del nemico: /assets/Black_Bony_Afterbirth.png");
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

            // Coordinate di default (Centro per la SpawnRoom)
            double spawnX = (roomWidth / 2) - (playerSize / 2);
            double spawnY = (roomHeight / 2) - (playerSize / 2);

            // Distanza dal bordo del muro quando si esce da una porta
            double padding = 50;

            // Se ci siamo mossi, nasciamo davanti alla porta corrispondente!
            if (lastEntryDirection != null) {
                switch (lastEntryDirection) {
                    case NORTH -> spawnY = roomHeight - playerSize - padding + 20; // Andati a nord -> Entrati da Sud
                    case SOUTH -> spawnY = padding - 20;                           // Andati a sud -> Entrati da Nord
                    case EAST -> spawnX = padding - 20;                            // Andati a est -> Entrati da Ovest
                    case WEST -> spawnX = roomWidth - playerSize - padding + 20;   // Andati a ovest -> Entrati da Est
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

    // ... [Metodi renderDoor identici a prima] ...
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
            // Aggiorniamo la memoria con la direzione appena presa!
            lastEntryDirection = dir;
            updateView();
        } else {
            System.out.println("Muro colpito, nemico vivo o chiave mancante!");
        }
    }
}