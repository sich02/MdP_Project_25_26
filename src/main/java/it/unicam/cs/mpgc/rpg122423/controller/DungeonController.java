package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.PlayerDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;

public class DungeonController {

    @FXML private Label hpLabel;
    @FXML private Label goldLabel;
    @FXML private Label floorLabel;
    @FXML private Pane roomPane;
    @FXML private Label keysLabel;

    private final DungeonService dungeonService;
    private final CombatUIManager combatUIManager;

    public DungeonController(DungeonService dungeonService, CombatUIManager combatUIManager) {
        this.dungeonService = dungeonService;
        this.combatUIManager = combatUIManager;
    }

    private Direction lastEntryDirection = null;
    private int selectedEnemyIndex = -1;

    @FXML
    public void initialize() {
        System.out.println("Dungeon UI caricata. Avvio Service...");
        dungeonService.startNewRun();
        updateView();
    }

    private void updateView() {
        PlayerDTO playerStats = dungeonService.getPlayerData();

        if (playerStats.currentHearts() <= 0) {
            hpLabel.setText("Cuori: 0.0 / " + playerStats.maxHearts());
            showGameOverScreen();
            return;
        }

        RoomDTO roomData = dungeonService.getCurrentRoomData();

        roomPane.getChildren().clear();

        RoomRenderer.renderFloor(roomPane);
        RoomRenderer.renderDoors(roomPane, roomData);

        if (roomData.enemies() != null && !roomData.enemies().isEmpty()) {
            if (selectedEnemyIndex < 0 || selectedEnemyIndex >= roomData.enemies().size()) {
                selectedEnemyIndex = RoomRenderer.calculateClosestEnemy(roomData.enemies(), lastEntryDirection);
            }
        }

        RoomRenderer.renderEnemies(roomPane, roomData.enemies(), selectedEnemyIndex, newIndex -> {
            if (selectedEnemyIndex != newIndex) {
                selectedEnemyIndex = newIndex;
                updateView();
            }
        }, roomData.isBossRoom());

        RoomRenderer.renderPlayer(roomPane, lastEntryDirection);

        // Renderizza la botola se il boss è stato sconfitto
        if (roomData.trapdoorActive()) {
            RoomRenderer.renderTrapdoor(roomPane, () -> {
                dungeonService.advanceFloor();
                lastEntryDirection = null;
                selectedEnemyIndex = -1;
                updateView();
            });
        }

        hpLabel.setText("Cuori: " + playerStats.currentHearts() + " / " + playerStats.maxHearts());
        goldLabel.setText("Oro: " + playerStats.gold());
        keysLabel.setText("Chiavi: " + playerStats.keys());
        floorLabel.setText("Piano: " + dungeonService.getCurrentFloorNumber());

        combatUIManager.render(roomPane, roomData, dungeonService, selectedEnemyIndex, this::updateView, this::startEnemyTurnSequence);
    }

    private void showGameOverScreen() {
        roomPane.getChildren().clear();
        Rectangle bg = new Rectangle(600, 400, Color.BLACK);

        Label deathLabel = new Label("SEI MORTO");
        deathLabel.setStyle("-fx-text-fill: #ff4c4c; -fx-font-size: 60px; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
        deathLabel.setLayoutX(130);
        deathLabel.setLayoutY(120);

        Button restartBtn = new Button("Ricomincia Partita");
        restartBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        restartBtn.setLayoutX(200);
        restartBtn.setLayoutY(220);

        restartBtn.setOnAction(e -> {
            lastEntryDirection = null;
            selectedEnemyIndex = -1;
            dungeonService.startNewRun();
            updateView();
        });

        roomPane.getChildren().addAll(bg, deathLabel, restartBtn);
    }

    @FXML private void moveNorth(ActionEvent event) { tryMove(Direction.NORTH); }
    @FXML private void moveSouth(ActionEvent event) { tryMove(Direction.SOUTH); }
    @FXML private void moveEast(ActionEvent event) { tryMove(Direction.EAST); }
    @FXML private void moveWest(ActionEvent event) { tryMove(Direction.WEST); }

    private void tryMove(Direction dir) {
        if (dungeonService.interactWithDirection(dir)) {
            lastEntryDirection = dir;
            selectedEnemyIndex = -1;
            updateView();
        } else {
            System.out.println("Muro colpito, nemico vivo o chiave mancante!");
        }
    }

    private void startEnemyTurnSequence() {
        String attackerName = dungeonService.getNextAttackerName();

        if (attackerName == null) {
            updateView();
            return;
        }
        DodgeQTEManager.showDodgeQTE(roomPane, attackerName, dodged -> {
            boolean enemyAttacked = dungeonService.executeNextEnemyTurn(dodged);
            updateView();

            if (enemyAttacked) {
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> startEnemyTurnSequence());
                pause.play();
            } else {
                updateView();
            }
        });
    }
}