package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
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

    @FXML
    public void initialize() {
        System.out.println("Dungeon UI caricata. Avvio Service...");
        dungeonService.startNewRun();
        updateView();
    }

    private void updateView() {
        RoomDTO roomData = dungeonService.getCurrentRoomData();

        // 0. Pulisce la stanza
        roomPane.getChildren().clear();

        // 1. Disegna il pavimento come strato base
        renderFloor();

        // 2. Disegna le porte sopra al pavimento in base ai dati del Service
        renderDoor(roomData.north(), Direction.NORTH);
        renderDoor(roomData.south(), Direction.SOUTH);
        renderDoor(roomData.east(), Direction.EAST);
        renderDoor(roomData.west(), Direction.WEST);

        // 3. Disegna il giocatore per ultimo (così sta sopra a tutto)
        spawnPlayer();

        // 4. Aggiornamento HUD
        PlayerDTO playerStats = dungeonService.getPlayerData();
        hpLabel.setText("Cuori: " + playerStats.currentHearts() + " / " + playerStats.maxHearts());
        goldLabel.setText("Oro: " + playerStats.gold());
        keysLabel.setText("Chiavi: " + playerStats.keys());
    }

    /**
     * Disegna il pavimento che copre l'intera stanza (600x400).
     */
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

    private void spawnPlayer() {
        double playerSize = 100;
        try {
            Image img = new Image(getClass().getResourceAsStream("/assets/player.png"));
            playerSprite = new ImageView(img);
            playerSprite.setFitWidth(playerSize);
            playerSprite.setFitHeight(playerSize);
            playerSprite.setPreserveRatio(true);
            playerSprite.setSmooth(false);

            playerSprite.setX((600 / 2) - (playerSize / 2));
            playerSprite.setY((400 / 2) - (playerSize / 2));

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
            updateView();
        } else {
            System.out.println("Muro colpito o ti serve una chiave per questa porta!");
        }
    }
}