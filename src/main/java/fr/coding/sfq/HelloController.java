package fr.coding.sfq;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void dishesPages() throws IOException {
//        welcomeText.setText("Here's the welcome text");
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        var fxmlLoader = new FXMLLoader(getClass().getResource("dishes-pages.fxml"));
        Scene disheScene = new Scene(fxmlLoader.load(), 300, 250);
        stage.setScene(disheScene);
        stage.show();
    }
}