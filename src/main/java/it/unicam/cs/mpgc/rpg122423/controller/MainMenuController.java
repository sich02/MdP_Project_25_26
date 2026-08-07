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
    private void handleNewRun(ActionEvent event) {
        System.out.println("Avvio Nuova Run! Generazione dungeon...");
        loadScene(event, "/dungeon.fxml");
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        System.out.println("Caricamento salvataggio... (Da implementare)");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.out.println("Chiusura in corso.");
        System.exit(0);
    }

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1024, 768));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}