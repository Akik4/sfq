package fr.coding.sfq;

import fr.coding.sfq.models.Dishes;
import fr.coding.sfq.models.Orders;
import fr.coding.sfq.models.Tables;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

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
    private ObservableList<Dishes> dishes = FXCollections.observableArrayList();


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

            Dishes Dnew = new Dishes("name", "description", 0, "");
            Dishes Dnew2 = new Dishes("name2", "description2", 2, "");
            Dishes Dnew3 = new Dishes("name3", "description3", 3, "");


            dishes.add(Dnew);
            dishes.add(Dnew2);
            dishes.add(Dnew3);


            Stage detailStage = new Stage();
            detailStage.setTitle("Détails de la commande");

            VBox mainLayout = new VBox(20);
            mainLayout.setStyle("-fx-padding: 20; -fx-background-color: #ffffff;");

            IntegerProperty totalPrice = new SimpleIntegerProperty(0);

            for (Dishes dish : dishes) {
                VBox dishCard = new VBox(10);
                dishCard.setStyle("-fx-padding: 15; -fx-border-color: black; -fx-background-color: white;");

                Text dishName = new Text(dish.getName());
                dishName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

                Text dishDescription = new Text(dish.getDescription());
                dishDescription.setStyle("-fx-font-size: 14px;");

                Text dishPrice = new Text(dish.getPrice() + " €");
                dishPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #28a745;");

                // Quantité + boutons
                HBox quantityBox = new HBox(10);
                quantityBox.setAlignment(Pos.CENTER);

                Button minusButton = new Button("−");
                Button plusButton = new Button("+");
                Text quantityText = new Text("0");

                plusButton.setOnAction(e -> {
                    int currentQty = Integer.parseInt(quantityText.getText());
                    quantityText.setText(String.valueOf(currentQty + 1));
                    totalPrice.set(totalPrice.get() + dish.getPrice());
                });

                minusButton.setOnAction(e -> {
                    int currentQty = Integer.parseInt(quantityText.getText());
                    if (currentQty > 0) {
                        quantityText.setText(String.valueOf(currentQty - 1));
                        totalPrice.set(totalPrice.get() - dish.getPrice());
                    }
                });

                quantityBox.getChildren().addAll(minusButton, quantityText, plusButton);

                dishCard.getChildren().addAll(dishName, dishDescription, dishPrice, quantityBox);
                mainLayout.getChildren().add(dishCard);
            }

            Button createOrderButton = new Button("Create Table");
            createOrderButton.setStyle("-fx-font-size: 14px;");
            createOrderButton.setOnAction(e -> createOrder());

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-font-size: 14px;");
            closeButton.setOnAction(event -> detailStage.close());


            Text totalText = new Text();
            totalText.textProperty().bind(Bindings.concat("Total price: ", totalPrice.asString(), " €"));
            totalText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            mainLayout.getChildren().addAll(closeButton, totalText);

            Scene detailScene = new Scene(mainLayout, 400, 500);
            detailStage.setScene(detailScene);
            detailStage.show();
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

    private void createOrder() {
        System.out.println("AHAHAHAHAHAHAHAHAH");
    }

}