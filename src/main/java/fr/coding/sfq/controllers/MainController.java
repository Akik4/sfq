package fr.coding.sfq.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Main application controller.
 * This class manages the main application window and view switching.
 * It implements the Singleton pattern to provide global access to view switching functionality.
 */
public class MainController {

    @FXML private VBox contentPane;

    private static MainController instance;
    @FXML private Button goToDishesButton;
    @FXML private Button tablesButton;
    @FXML private Button employeeButton;
    @FXML private Button transactionsButton;
    @FXML private Button ordersButton;

    public MainController() {
        instance = this; // Store a reference to this controller
    }

    /**
     * Initializes the main controller.
     * This method:
     * - Sets up navigation buttons
     * - Configures event handlers
     * - Loads the home page by default
     */
    @FXML
    public void initialize() {
        goToDishesButton.setOnAction(event -> MainController.getInstance().switchView("DishView.fxml"));
        tablesButton.setOnAction(event -> MainController.getInstance().switchView("TableView.fxml"));
        employeeButton.setOnAction(event -> MainController.getInstance().switchView("EmployeeView.fxml"));
        transactionsButton.setOnAction(event -> MainController.getInstance().switchView("TransactionView.fxml"));
        ordersButton.setOnAction(event -> MainController.getInstance().switchView("OrdersView.fxml"));
        switchView("HomePage.fxml"); // Loads homepage by default
    }

    /**
     * Switches the current view to the specified FXML file.
     * 
     * @param fxml The path to the FXML file to load
     */
    public void switchView(String fxml) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fr/coding/sfq/"+fxml));
            contentPane.getChildren().setAll(view); // Replace content properly
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the singleton instance of the MainController.
     * 
     * @return The MainController instance
     */
    public static MainController getInstance() {
        return instance;
    }
}