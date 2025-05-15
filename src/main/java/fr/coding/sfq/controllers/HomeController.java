package fr.coding.sfq.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the home page.
 * This class manages the main navigation of the application.
 * It provides access to all main features through a dashboard button.
 */
public class HomeController {

    @FXML private Button dashboardButton;

    /**
     * Initializes the home controller.
     * This method:
     * - Sets up the dashboard button
     * - Configures navigation to the dashboard view
     */
    @FXML
    public void initialize() {
        dashboardButton.setOnAction(event -> {MainController.getInstance().switchView("AllDashboardView.fxml");});
    }
}