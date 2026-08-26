package it.unicam.cs.mpgc.rpg122423.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private javafx.scene.control.Button continueButton;
    
    @FXML
    private javafx.scene.layout.VBox characterSelectionOverlay;
    
    private it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter selectedCharacter = null;
    
    private final it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService saveService = new it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService();
    private boolean hasSave = false;
    private it.unicam.cs.mpgc.rpg122423.entity.SaveGame saveGame = null;

    @FXML
    public void initialize() {
        java.util.Optional<it.unicam.cs.mpgc.rpg122423.entity.SaveGame> loadedSave = saveService.loadGame();
        if (loadedSave.isPresent()) {
            hasSave = true;
            saveGame = loadedSave.get();
            continueButton.setDisable(false);
        } else {
            continueButton.setDisable(true);
        }
    }

    @FXML
    private void handleNewRun(ActionEvent event) {
        System.out.println("Apertura menu di selezione personaggio...");
        if (characterSelectionOverlay != null) {
            characterSelectionOverlay.setVisible(true);
        } else {
            // Fallback se l'overlay non è stato caricato dall'FXML
            startGameWithCharacter(event, it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter.KNIGHT);
        }
    }

    @FXML
    private void selectKnight(ActionEvent event) {
        startGameWithCharacter(event, it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter.KNIGHT);
    }

    @FXML
    private void selectRogue(ActionEvent event) {
        startGameWithCharacter(event, it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter.ROGUE);
    }

    @FXML
    private void selectMage(ActionEvent event) {
        startGameWithCharacter(event, it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter.MAGE);
    }

    @FXML
    private void closeCharacterSelection(ActionEvent event) {
        if (characterSelectionOverlay != null) characterSelectionOverlay.setVisible(false);
    }
    
    private void startGameWithCharacter(ActionEvent event, it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter character) {
        this.selectedCharacter = character;
        System.out.println("Avvio Nuova Run con personaggio: " + character.getDisplayName());
        loadScene(event, "/dungeon.fxml", false);
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        System.out.println("Caricamento salvataggio...");
        if (hasSave) {
            loadScene(event, "/dungeon.fxml", true);
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.out.println("Chiusura in corso.");
        System.exit(0);
    }

    private void loadScene(ActionEvent event, String fxmlPath, boolean isLoad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            
            if (fxmlPath.equals("/dungeon.fxml")) {
                loader.setControllerFactory(clazz -> {
                    if (clazz == DungeonController.class) {
                        it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService dungeonService = new it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService(saveService);
                        if (isLoad && saveGame != null) {
                            dungeonService.restoreGame(saveGame);
                        }
                        return new DungeonController(dungeonService, new CombatUIManager(), selectedCharacter, saveService);
                    }
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1024, 768));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}