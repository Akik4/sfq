package fr.coding.sfq;

import fr.coding.sfq.models.Dishes;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DishController {

    @FXML private TableView<Dishes> dishTable;
    @FXML private TableColumn<Dishes, String> nameColumn;
    @FXML private TableColumn<Dishes, String> descriptionColumn;
    @FXML private TableColumn<Dishes, Integer> priceColumn;
    @FXML private Button homeButton;
    @FXML private Button addDishButton;
    @FXML private Button removeDishButton;

    private ObservableList<Dishes> dishes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        priceColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()).asObject());

        dishTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dishTable.setItems(dishes);

        addDishButton.setOnAction(event -> addDish());
        removeDishButton.setOnAction(event -> removeDish());
        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));
    }

    private void addDish() {
        dishes.add(new Dishes("New Dish", "Delicious meal", 10, ""));
    }

    private void removeDish() {
        if (!dishes.isEmpty()) {
            dishes.remove(dishes.size() - 1);
        }
    }
}