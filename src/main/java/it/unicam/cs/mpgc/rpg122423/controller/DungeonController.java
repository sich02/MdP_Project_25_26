package it.unicam.cs.mpgc.rpg122423.controller;

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

    private ImageView playerSprite;

    // Istanziamo il Service che fa da tramite con la logica procedurale
    private final DungeonService dungeonService = new DungeonService();

    @FXML
    public void initialize() {
        System.out.println("Dungeon UI caricata. Avvio Service...");
        dungeonService.startNewRun();
        updateView();
    }

    /**
     * Sincronizza la grafica chiedendo i dati (DTO) al Service.
     */
    private void updateView() {
        // Riceviamo il DTO, non tocchiamo mai il Model direttamente!
        RoomDTO roomData = dungeonService.getCurrentRoomData();

        // Usiamo i metodi generati in automatico dal record RoomDTO
        renderRoom(roomData.hasNorth(), roomData.hasSouth(), roomData.hasEast(), roomData.hasWest());
        spawnPlayer();
    }

    private void renderRoom(boolean hasNorth, boolean hasSouth, boolean hasEast, boolean hasWest) {
        roomPane.getChildren().clear();

        double roomWidth = 600;
        double roomHeight = 400;
        double doorSize = 60;
        double doorDepth = 15;

        if (hasNorth) {
            Rectangle door = new Rectangle(doorSize, doorDepth, Color.SADDLEBROWN);
            door.setX((roomWidth / 2) - (doorSize / 2));
            door.setY(-doorDepth);
            roomPane.getChildren().add(door);
        }
        if (hasSouth) {
            Rectangle door = new Rectangle(doorSize, doorDepth, Color.SADDLEBROWN);
            door.setX((roomWidth / 2) - (doorSize / 2));
            door.setY(roomHeight);
            roomPane.getChildren().add(door);
        }
        if (hasEast) {
            Rectangle door = new Rectangle(doorDepth, doorSize, Color.SADDLEBROWN);
            door.setX(roomWidth);
            door.setY((roomHeight / 2) - (doorSize / 2));
            roomPane.getChildren().add(door);
        }
        if (hasWest) {
            Rectangle door = new Rectangle(doorDepth, doorSize, Color.SADDLEBROWN);
            door.setX(-doorDepth);
            door.setY((roomHeight / 2) - (doorSize / 2));
            roomPane.getChildren().add(door);
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

    @FXML private void moveNorth(ActionEvent event) { tryMove(Direction.NORTH); }
    @FXML private void moveSouth(ActionEvent event) { tryMove(Direction.SOUTH); }
    @FXML private void moveEast(ActionEvent event) { tryMove(Direction.EAST); }
    @FXML private void moveWest(ActionEvent event) { tryMove(Direction.WEST); }

    private void tryMove(Direction dir) {
        if (dungeonService.movePlayer(dir)) {
            System.out.println("Spostamento verso " + dir + " riuscito!");
            updateView();
        } else {
            System.out.println("Muro colpito!");
        }
    }
}