package fr.coding.sfq;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {

    @FXML private Button goToDishesButton;
    @FXML private Button tablesButton;
    @FXML private Button employeeButton;

    @FXML
    public void initialize() {
        goToDishesButton.setOnAction(event -> MainController.getInstance().switchView("DishView.fxml"));
        tablesButton.setOnAction(event -> MainController.getInstance().switchView("TableView.fxml"));
        employeeButton.setOnAction(event -> MainController.getInstance().switchView("EmployeeView.fxml"));
    }
}