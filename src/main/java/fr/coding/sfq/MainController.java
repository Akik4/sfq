package fr.coding.sfq;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML private VBox contentPane;

    private static MainController instance;

    public MainController() {
        instance = this; // Store a reference to this controller
    }

    @FXML
    public void initialize() {
        switchView("HomePage.fxml"); // Loads homepage by default
    }


    public static MainController getInstance() {
        return instance;
    }

    public void switchView(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxml));
            contentPane.getChildren().setAll(view); // Replace content properly
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}