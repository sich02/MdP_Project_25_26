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
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;
import java.util.List;

public class DungeonController {

    @FXML private HBox heartsBox;
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
            updateHeartsUI(0, playerStats.maxHearts());
            showGameOverScreen();
            return;
        }

        RoomDTO roomData = dungeonService.getCurrentRoomData();

        roomPane.getChildren().clear();

        RoomRenderer.renderFloor(roomPane);
        RoomRenderer.renderDoors(roomPane, roomData, this::tryMove);

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

        RoomRenderer.renderLoot(roomPane, roomData, () -> {
            it.unicam.cs.mpgc.rpg122423.model.item.Item claimedItem = dungeonService.claimLootInCurrentRoom();
            if (claimedItem != null) {
                if (claimedItem instanceof it.unicam.cs.mpgc.rpg122423.model.item.ElementalItem elementalItem) {
                    showElementalSelectionUI(elementalItem.getElement());
                } else {
                    updateView();
                }
            }
        });

        updateHeartsUI(playerStats.currentHearts(), playerStats.maxHearts());
        goldLabel.setText(String.valueOf(playerStats.gold()));
        keysLabel.setText(String.valueOf(playerStats.keys()));
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

    private void updateHeartsUI(double currentHearts, double maxHearts) {
        heartsBox.getChildren().clear();

        int fullHearts = (int) currentHearts;
        boolean hasHalfHeart = (currentHearts - fullHearts) >= 0.5;
        int emptyHearts = (int) (maxHearts - fullHearts - (hasHalfHeart ? 1 : 0));

        Image fullHeartImg = new Image(getClass().getResource("/assets/heart_full.png").toExternalForm());
        Image halfHeartImg = new Image(getClass().getResource("/assets/heart_half.png").toExternalForm());
        Image emptyHeartImg = new Image(getClass().getResource("/assets/heart_empty.png").toExternalForm());

        for (int i = 0; i < fullHearts; i++) {
            ImageView view = new ImageView(fullHeartImg);
            view.setFitWidth(24);
            view.setFitHeight(24);
            heartsBox.getChildren().add(view);
        }

        if (hasHalfHeart) {
            ImageView view = new ImageView(halfHeartImg);
            view.setFitWidth(24);
            view.setFitHeight(24);
            heartsBox.getChildren().add(view);
        }

        for (int i = 0; i < emptyHearts; i++) {
            ImageView view = new ImageView(emptyHeartImg);
            view.setFitWidth(24);
            view.setFitHeight(24);
            heartsBox.getChildren().add(view);
        }
    }



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

            if (dungeonService.getPlayerData().currentHearts() <= 0) {
                return; // Stop the sequence if the player died from this attack
            }

            if (enemyAttacked) {
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> startEnemyTurnSequence());
                pause.play();
            } else {
                updateView();
            }
        });
    }

    private void showElementalSelectionUI(it.unicam.cs.mpgc.rpg122423.model.dice.Element element) {
        javafx.scene.layout.VBox overlay = new javafx.scene.layout.VBox(20);
        overlay.setAlignment(javafx.geometry.Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 30; -fx-border-color: " + (element == it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE ? "orange" : "white") + "; -fx-border-width: 3; -fx-background-radius: 10; -fx-border-radius: 10;");
        
        Label title = new Label("Scegli un dado da incantare con: " + element.name());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        javafx.scene.layout.HBox diceBox = new javafx.scene.layout.HBox(10);
        diceBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        List<Integer> diceValues = dungeonService.getPlayerDiceValues();
        List<it.unicam.cs.mpgc.rpg122423.model.dice.Element> diceElements = dungeonService.getPlayerDiceElements();
        
        for (int i = 0; i < diceValues.size(); i++) {
            int index = i;
            javafx.scene.layout.StackPane diePane = new javafx.scene.layout.StackPane();
            
            Rectangle bg = new Rectangle(50, 50);
            bg.setArcWidth(10);
            bg.setArcHeight(10);
            bg.setFill(Color.WHITE);
            
            if (diceElements.get(i) == it.unicam.cs.mpgc.rpg122423.model.dice.Element.FIRE) {
                bg.setStroke(Color.ORANGERED);
                bg.setStrokeWidth(3);
            }
            
            Label valueLabel = new Label(String.valueOf(diceValues.get(i)));
            valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            
            diePane.getChildren().addAll(bg, valueLabel);
            diePane.setCursor(Cursor.HAND);
            
            diePane.setOnMouseClicked(e -> {
                dungeonService.setPlayerDiceElement(index, element);
                updateView();
            });
            
            diceBox.getChildren().add(diePane);
        }
        
        overlay.getChildren().addAll(title, diceBox);
        
        // Posiziona al centro della stanza
        overlay.setLayoutX(100);
        overlay.setLayoutY(120);
        
        roomPane.getChildren().add(overlay);
    }
}