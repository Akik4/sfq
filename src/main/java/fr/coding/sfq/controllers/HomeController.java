package fr.coding.sfq.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {

    @FXML private Button dashboardButton;

    @FXML
    public void initialize() {
        dashboardButton.setOnAction(event -> {MainController.getInstance().switchView("AllDashboardView.fxml");});
    }
}