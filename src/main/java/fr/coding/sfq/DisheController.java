package fr.coding.sfq;

import fr.coding.sfq.models.Dishes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DisheController {

    @FXML private TableView<Dishes> dishTable;
    @FXML private TableColumn<Dishes, String> nameColumn;
    @FXML private TableColumn<Dishes, Integer> priceColumn;
    @FXML private TableColumn<Dishes, String> descriptionColumn;
    @FXML private TableColumn<Dishes, String> imageColumn;

    private ObservableList<Dishes> dishes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Map columns to Dish properties
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        imageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImage()));

        // Sample data
        dishes.add(new Dishes("Pasta Carbonara", "Creamy sauce with bacon", 12, "pasta.jpg"));
        dishes.add(new Dishes("Margherita Pizza", "Tomato, mozzarella, basil", 9, "pizza.jpg"));

        dishTable.setItems(dishes);
    }
}
