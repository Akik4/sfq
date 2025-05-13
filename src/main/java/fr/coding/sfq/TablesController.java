package fr.coding.sfq;

import fr.coding.sfq.models.Orders;
import fr.coding.sfq.models.Tables;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TablesController {

    @FXML private TableView<Tables> tablesTable;
    @FXML private TableColumn<Tables, String> tableNumberColumn;
    @FXML private TableColumn<Tables, Boolean> statusColumn;
    @FXML private TableColumn<Tables, String> assignedOrderColumn;

    @FXML private Button markAvailableButton;
    @FXML private Button markOccupiedButton;
    @FXML private Button assignOrderButton;
    @FXML private TextField tableNumberField;
    @FXML private Button createTableButton;



    @FXML private Button homeButton;

    private ObservableList<Tables> tables = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tableNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        statusColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isOccupied()));
//        assignedOrderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAssignedOrder()));

        tablesTable.setItems(tables);

        markAvailableButton.setOnAction(event -> markTableAvailable());
        markOccupiedButton.setOnAction(event -> markTableOccupied());
        assignOrderButton.setOnAction(event -> assignOrder());
        createTableButton.setOnAction(event -> createTable());


        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));
    }

    private void markTableAvailable() {
        Tables selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setOccupied(false);
            tablesTable.refresh();
        }
    }

    private void markTableOccupied() {
        Tables selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setOccupied(true);
            tablesTable.refresh();
        }
    }

    private void assignOrder() {
        Tables selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
//            selectedTable.setAssignedOrder("Commande #" + (int)(Math.random() * 100));
            tablesTable.refresh();
        }
    }

    private void createTable() {
        String tableNumber;

        try {
            tableNumber = tableNumberField.getText();
        } catch (NumberFormatException e) {
            System.out.println("Numéro de table invalide !");
            return;
        }

//        Orders t = new Orders()

        Tables newTable = new Tables(4, tableNumber, false, null);
        tables.add(newTable);
        tablesTable.refresh(); // Refresh TableView to show new tables

        tableNumberField.clear();
    }

}