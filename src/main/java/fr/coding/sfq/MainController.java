package fr.coding.sfq;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML private VBox contentPane;

    private static MainController instance;
    @FXML private Button goToDishesButton;
    @FXML private Button tablesButton;
    @FXML private Button employeeButton;
    @FXML private Button transactionsButton;

    public MainController() {
        instance = this; // Store a reference to this controller
    }

    @FXML
    public void initialize() {
        goToDishesButton.setOnAction(event -> MainController.getInstance().switchView("DishView.fxml"));
        tablesButton.setOnAction(event -> MainController.getInstance().switchView("TableView.fxml"));
        employeeButton.setOnAction(event -> MainController.getInstance().switchView("EmployeeView.fxml"));
        transactionsButton.setOnAction(event -> MainController.getInstance().switchView("TransactionView.fxml"));

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