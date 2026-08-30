package it.unicam.cs.mpgc.rpg122423.controller;

import it.unicam.cs.mpgc.rpg122423.service.persistence.HibernateUtil;
import it.unicam.cs.mpgc.rpg122423.service.persistence.SaveService;
import it.unicam.cs.mpgc.rpg122423.model.combat.PlayableCharacter;
import it.unicam.cs.mpgc.rpg122423.entity.SaveGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller del menu principale. Le dipendenze (HibernateUtil, SaveService)
 * vengono create qui come punto di composizione dell'applicazione.
 */
public class MainMenuController {

    @FXML
    private Button continueButton;

    @FXML
    private VBox characterSelectionOverlay;

    private PlayableCharacter selectedCharacter = null;

    private final HibernateUtil hibernateUtil = new HibernateUtil();
    private final SaveService saveService = new SaveService(hibernateUtil);
    private boolean hasSave = false;
    private SaveGame saveGame = null;

    @FXML
    public void initialize() {
        Optional<SaveGame> loadedSave = saveService.loadGame();
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
        if (characterSelectionOverlay != null) {
            characterSelectionOverlay.setVisible(true);
        } else {
            startGameWithCharacter(event, PlayableCharacter.KNIGHT);
        }
    }

    @FXML
    private void selectKnight(ActionEvent event) {
        startGameWithCharacter(event, PlayableCharacter.KNIGHT);
    }

    @FXML
    private void selectRogue(ActionEvent event) {
        startGameWithCharacter(event, PlayableCharacter.ROGUE);
    }

    @FXML
    private void selectMage(ActionEvent event) {
        startGameWithCharacter(event, PlayableCharacter.MAGE);
    }

    @FXML
    private void closeCharacterSelection(ActionEvent event) {
        if (characterSelectionOverlay != null) characterSelectionOverlay.setVisible(false);
    }

    private void startGameWithCharacter(ActionEvent event, PlayableCharacter character) {
        this.selectedCharacter = character;
        loadScene(event, "/dungeon.fxml", false);
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        if (hasSave) {
            loadScene(event, "/dungeon.fxml", true);
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }

    private void loadScene(ActionEvent event, String fxmlPath, boolean isLoad) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if ("/dungeon.fxml".equals(fxmlPath)) {
                loader.setControllerFactory(clazz -> {
                    if (clazz == DungeonController.class) {
                        it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService dungeonService =
                                new it.unicam.cs.mpgc.rpg122423.service.dungeon.DungeonService(saveService);
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