package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.dto.DoorDTO;
import it.unicam.cs.mpgc.rpg122423.dto.EnemyDTO;
import it.unicam.cs.mpgc.rpg122423.dto.PlayerDTO;
import it.unicam.cs.mpgc.rpg122423.dto.RoomDTO;
import it.unicam.cs.mpgc.rpg122423.model.dungeon.Direction;
import it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javafx.geometry.Pos; // Importante per centrare il testo sotto i dadi!
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

    // --- MEMORIA DI STATO DEL COMBATTIMENTO E DEI DADI ---
    private boolean hasPlayerRolled = false;
    private boolean hasPlayerAttacked = false;
    private int[] currentDiceRolls = {1, 1, 1, 1, 1};

    // Logica Reroll
    private int rerollsLeft = 3; // Quante volte puoi cliccare un dado singolo per rerollarlo
    private boolean isAnimating = false;

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
        renderFloor();

        renderDoor(roomData.north(), Direction.NORTH);
        renderDoor(roomData.south(), Direction.SOUTH);
        renderDoor(roomData.east(), Direction.EAST);
        renderDoor(roomData.west(), Direction.WEST);

        spawnEnemies(roomData.enemies());
        spawnPlayer();

        hpLabel.setText("Cuori: " + playerStats.currentHearts() + " / " + playerStats.maxHearts());
        goldLabel.setText("Oro: " + playerStats.gold());
        keysLabel.setText("Chiavi: " + playerStats.keys());

        renderCombatUI(roomData);
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
            resetPlayerTurnState();
            lastEntryDirection = null;
            dungeonService.startNewRun();
            updateView();
        });

        roomPane.getChildren().addAll(bg, deathLabel, restartBtn);
    }

    private void resetPlayerTurnState() {
        hasPlayerRolled = false;
        hasPlayerAttacked = false;
        currentDiceRolls = new int[]{1, 1, 1, 1, 1};
        rerollsLeft = 3;
        isAnimating = false;
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
            Rectangle fallback = new Rectangle(600, 400, Color.web("#6d4a3d"));
            roomPane.getChildren().add(fallback);
        }
    }

    private void spawnEnemies(java.util.List<EnemyDTO> enemies) {
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

                ImageView enemySprite = new ImageView(new Image(getClass().getResourceAsStream("/assets/" + spriteName)));
                enemySprite.setFitWidth(enemySize);
                enemySprite.setPreserveRatio(true);
                enemySprite.setSmooth(false);
                enemySprite.setX(positions[i][0]);
                enemySprite.setY(positions[i][1]);
                roomPane.getChildren().add(enemySprite);

                Label hpText = new Label("HP: " + enemy.currentHp() + "/" + enemy.maxHp());
                hpText.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
                hpText.setLayoutX(positions[i][0] - 5);
                hpText.setLayoutY(positions[i][1] - 15);

                Label intentText = new Label(enemy.intentDescription());
                intentText.setStyle("-fx-text-fill: #fcdb03; -fx-font-family: 'Courier New'; -fx-font-size: 10px;");
                intentText.setLayoutX(positions[i][0] - 25);
                intentText.setLayoutY(positions[i][1] - 30);
                roomPane.getChildren().addAll(hpText, intentText);
            }
        } catch (Exception e) {}
    }

    private void spawnPlayer() {
        double playerSize = 100;
        try {
            ImageView playerSprite = new ImageView(new Image(getClass().getResourceAsStream("/assets/player.png")));
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
        } catch (Exception e) {
            Rectangle placeholder = new Rectangle(playerSize, playerSize, Color.DEEPSKYBLUE);
            placeholder.setX((600 / 2.0) - (playerSize / 2.0));
            placeholder.setY((400 / 2.0) - (playerSize / 2.0));
            roomPane.getChildren().add(placeholder);
        }
    }

    private void renderDoor(DoorDTO doorInfo, Direction dir) {
        if (!doorInfo.exists()) return;
        double doorSize = 60;
        String imagePath = "/assets/floorDoor.png";
        switch (doorInfo.roomType()) {
            case "BOSS" -> imagePath = "/assets/boosRoom Opened.png";
            case "TREASURE" -> imagePath = doorInfo.isLocked() ? "/assets/treasure locked.png" : "/assets/treasure opened.png";
            case "SHOP" -> imagePath = doorInfo.isLocked() ? "/assets/shop locked.png" : "/assets/shop opened.png";
        }
        try {
            ImageView doorSprite = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
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

    @FXML private void moveNorth(ActionEvent event) { tryMove(Direction.NORTH); }
    @FXML private void moveSouth(ActionEvent event) { tryMove(Direction.SOUTH); }
    @FXML private void moveEast(ActionEvent event) { tryMove(Direction.EAST); }
    @FXML private void moveWest(ActionEvent event) { tryMove(Direction.WEST); }

    private void tryMove(Direction dir) {
        if (dungeonService.interactWithDirection(dir)) {
            lastEntryDirection = dir;
            resetPlayerTurnState();
            updateView();
        } else {
            System.out.println("Muro colpito, nemico vivo o chiave mancante!");
        }
    }

    private void startEnemyTurnSequence() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (dungeonService.getPlayerData().currentHearts() <= 0) return;

            boolean enemyAttacked = dungeonService.executeNextEnemyTurn();
            updateView();

            if (enemyAttacked) {
                startEnemyTurnSequence();
            } else {
                resetPlayerTurnState();
                updateView();
            }
        });
        pause.play();
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

    // --- ANIMAZIONE: TIRO A CASCATA (TUTTI E 5 I DADI) ---
    private void playCascadingRollAnimation(int index, ImageView[] diceViews, Label[] diceLabels, Label totalLabel, Runnable onComplete) {
        if (index >= 5) {
            if (onComplete != null) onComplete.run();
            return;
        }

        Timeline timeline = new Timeline();
        Random rand = new Random();
        int frames = 6;

        // Animazione delle facce che girano
        for (int i = 0; i < frames; i++) {
            KeyFrame kf = new KeyFrame(Duration.millis(50 * i), e -> {
                int randomFace = rand.nextInt(6) + 1;
                diceViews[index].setImage(getDiceImage(randomFace));
                diceLabels[index].setText("?");
            });
            timeline.getKeyFrames().add(kf);
        }

        // Frame finale: si ferma sul valore vero e innesca il dado successivo
        KeyFrame finalKf = new KeyFrame(Duration.millis(50 * frames), e -> {
            currentDiceRolls[index] = rand.nextInt(6) + 1; // Salva il numero reale
            diceViews[index].setImage(getDiceImage(currentDiceRolls[index]));
            diceLabels[index].setText("+" + currentDiceRolls[index]); // Mostra il danno

            // Aggiorna in tempo reale il totale a sinistra man mano che i dadi si fermano
            int currentTotal = 0;
            for (int j = 0; j <= index; j++) currentTotal += currentDiceRolls[j];
            totalLabel.setText("TOTALE:\n" + currentTotal);

            playCascadingRollAnimation(index + 1, diceViews, diceLabels, totalLabel, onComplete);
        });

        timeline.getKeyFrames().add(finalKf);
        timeline.play();
    }

    // --- ANIMAZIONE: REROLL DEL SINGOLO DADO CLICCATO ---
    private void playSingleDiceRollAnimation(int index, ImageView diceView, Label diceLabel, Label totalLabel, Runnable onComplete) {
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
            currentDiceRolls[index] = rand.nextInt(6) + 1;
            diceView.setImage(getDiceImage(currentDiceRolls[index]));
            diceLabel.setText("+" + currentDiceRolls[index]);

            // Ricalcola il totale
            int newTotal = 0;
            for (int d : currentDiceRolls) newTotal += d;
            totalLabel.setText("TOTALE:\n" + newTotal);

            if (onComplete != null) onComplete.run();
        });

        timeline.getKeyFrames().add(finalKf);
        timeline.play();
    }

    private void renderCombatUI(RoomDTO roomData) {
        if (roomData.enemies() == null || roomData.enemies().isEmpty()) return;

        Pane combatMenu = new Pane();

        if ("ENEMY_TURN".equals(roomData.combatPhase())) {
            Label enemyTurnLabel = new Label("⌛ Turno dei Nemici in corso...");
            enemyTurnLabel.setStyle("-fx-text-fill: #ff4c4c; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 5px;");
            enemyTurnLabel.setLayoutX(160);
            enemyTurnLabel.setLayoutY(350);
            combatMenu.getChildren().add(enemyTurnLabel);

        } else {
            // Contenitore principale orizzontale (Totale a SX, Dadi a DX)
            HBox mainDiceArea = new HBox(15);
            mainDiceArea.setAlignment(Pos.CENTER_LEFT);
            mainDiceArea.setLayoutX(100);
            mainDiceArea.setLayoutY(330);

            // 1. BLOCCO DEL TOTALE (A SINISTRA)
            int totalDmg = 0;
            for (int d : currentDiceRolls) totalDmg += d;

            Label totalLabel = new Label("TOTALE:\n" + (hasPlayerRolled ? totalDmg : "0"));
            totalLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px; -fx-border-color: #4CAF50; -fx-border-width: 2px;");
            totalLabel.setAlignment(Pos.CENTER);

            // 2. BLOCCO DEI DADI
            HBox diceBox = new HBox(8);
            ImageView[] diceViews = new ImageView[5];
            Label[] diceLabels = new Label[5];

            for (int i = 0; i < 5; i++) {
                VBox singleDiceBox = new VBox(2);
                singleDiceBox.setAlignment(Pos.CENTER);

                ImageView diceSprite = new ImageView();
                diceSprite.setFitWidth(40);
                diceSprite.setFitHeight(40);
                diceSprite.setPreserveRatio(true);
                diceSprite.setSmooth(false);
                diceSprite.setImage(getDiceImage(currentDiceRolls[i]));
                diceViews[i] = diceSprite;

                // Etichetta del danno (es. "+3")
                Label diceDmgLabel = new Label(hasPlayerRolled ? "+" + currentDiceRolls[i] : "");
                diceDmgLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
                diceLabels[i] = diceDmgLabel;

                // LOGICA DEL CLICK: REROLL DEL SINGOLO DADO
                final int finalI = i;
                diceSprite.setOnMouseClicked(e -> {
                    if (hasPlayerRolled && !hasPlayerAttacked && rerollsLeft > 0 && !isAnimating) {
                        isAnimating = true;
                        rerollsLeft--;

                        // Aggiorniamo la grafica senza pulire lo schermo per evitare sfarfallii
                        playSingleDiceRollAnimation(finalI, diceViews[finalI], diceLabels[finalI], totalLabel, () -> {
                            isAnimating = false;
                            updateView(); // Refresh completo solo alla fine dell'animazione
                        });

                        updateView(); // Ricarica subito per mostrare i bottoni disabilitati durante l'animazione e aggiornare "Reroll rimasti"
                    }
                });

                singleDiceBox.getChildren().addAll(diceSprite, diceDmgLabel);
                diceBox.getChildren().add(singleDiceBox);
            }

            mainDiceArea.getChildren().addAll(totalLabel, diceBox);

            // 3. BLOCCO DEI BOTTONI (A DESTRA)
            VBox buttonsBox = new VBox(8);
            buttonsBox.setAlignment(Pos.CENTER);
            buttonsBox.setLayoutX(440);
            buttonsBox.setLayoutY(285);

            Label rerollInfo = new Label("Reroll: " + rerollsLeft);
            rerollInfo.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

            Button rollBtn = new Button("🎲 Tira i Dadi");
            Button attackBtn = new Button("⚔ Attacca");
            Button endTurnBtn = new Button("⧖ Fine Turno");

            rollBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
            attackBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            endTurnBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold;");

            // Stato bottoni
            rollBtn.setDisable(hasPlayerRolled || isAnimating);
            attackBtn.setDisable(!hasPlayerRolled || hasPlayerAttacked || isAnimating);
            endTurnBtn.setDisable(!hasPlayerAttacked || isAnimating);

            rollBtn.setOnAction(e -> {
                isAnimating = true;
                rollBtn.setDisable(true);
                attackBtn.setDisable(true);
                endTurnBtn.setDisable(true);

                // Avvia l'animazione a cascata sul primo dado (indice 0)
                playCascadingRollAnimation(0, diceViews, diceLabels, totalLabel, () -> {
                    isAnimating = false;
                    hasPlayerRolled = true;
                    updateView();
                });
            });

            attackBtn.setOnAction(e -> {
                int dmg = 0;
                for (int d : currentDiceRolls) dmg += d;
                dungeonService.executePlayerAttack(dmg);
                hasPlayerAttacked = true;
                updateView();
            });

            endTurnBtn.setOnAction(e -> {
                dungeonService.endPlayerTurn();
                updateView();
                startEnemyTurnSequence();
            });

            buttonsBox.getChildren().addAll(rerollInfo, rollBtn, attackBtn, endTurnBtn);
            combatMenu.getChildren().addAll(mainDiceArea, buttonsBox);
        }

        roomPane.getChildren().add(combatMenu);
    }
}