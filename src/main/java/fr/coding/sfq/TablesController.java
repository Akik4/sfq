package fr.coding.sfq;

import fr.coding.sfq.models.*;
import fr.coding.sfq.util.HibernateUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TablesController {

    @FXML private TableView<TablesEntity> tablesTable;
    @FXML private TableColumn<TablesEntity, String> tableNumberColumn;
    @FXML private TableColumn<TablesEntity, Boolean> statusColumn;
    @FXML private TableColumn<TablesEntity, String> assignedOrderColumn;

    @FXML private Button markAvailableButton;
    @FXML private Button markOccupiedButton;
    @FXML private Button assignOrderButton;
    @FXML private TextField tableNumberField;
    @FXML private Button createTableButton;



    @FXML private Button homeButton;

    private ObservableList<TablesEntity> tables = FXCollections.observableArrayList();
    private ObservableList<DishesEntity> dishes = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        tableNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        statusColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isOccupied()));
        assignedOrderColumn.setCellValueFactory(cellData -> {
            OrdersEntity order = cellData.getValue().getOrder();
            return new SimpleStringProperty(order != null ? "Commande #" + order.getId() : "Aucune");
        });
        tablesTable.setItems(tables);

        markAvailableButton.setOnAction(event -> markTableAvailable());
        markOccupiedButton.setOnAction(event -> markTableOccupied());
        assignOrderButton.setOnAction(event -> assignOrder());
        createTableButton.setOnAction(event -> createTable());


        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<TablesEntity> result = session.createQuery("FROM TablesEntity", TablesEntity.class).list();
            tables.setAll(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markTableAvailable() {
        TablesEntity selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setOccupied(false);
            tablesTable.refresh();
        }
    }

    private void markTableOccupied() {
        TablesEntity selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setOccupied(true);
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

        TablesEntity newTable = new TablesEntity(4, tableNumber, false);
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            TablesEntity table = new TablesEntity(newTable.getSize(), newTable.getLocation(), newTable.isOccupied());
            session.save(table);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tables.add(newTable);
        tablesTable.refresh(); // Refresh TableView to show new tables

        tableNumberField.clear();
    }

    private void createOrder(double totalPrice, TablesEntity table, List<DishesEntity> selectedDishes) {
        OrdersEntity newOrder = new OrdersEntity(new Date(), false, totalPrice);
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Step 1
            session.save(newOrder);

            // Step 2
            table.setOrder(newOrder);
            table.setOccupied(true);
            session.update(table);

            // Step 3
            for (DishesEntity dish : selectedDishes) {
                OrderDishiesEntity orderDish = new OrderDishiesEntity();
                orderDish.setDish(dish);
                orderDish.setOrder(newOrder);
                session.save(orderDish);
            }

            tx.commit();
            System.out.println("Commande créée avec ID = " + newOrder.getId());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    private void assignOrder() {
        TablesEntity selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        List<DishesEntity> selectedDishes = new ArrayList<>();

        if (selectedTable != null) {
//            selectedTable.setAssignedOrder("Commande #" + (int)(Math.random() * 100));
            tablesTable.refresh();

            DishesEntity Dnew = new DishesEntity("name", "description", 0, "");
            DishesEntity Dnew2 = new DishesEntity("name2", "description2", 2, "");
            DishesEntity Dnew3 = new DishesEntity("name3", "description3", 3, "");


            dishes.add(Dnew);
            dishes.add(Dnew2);
            dishes.add(Dnew3);


            Stage detailStage = new Stage();
            detailStage.setTitle("Détails de la commande");

            VBox mainLayout = new VBox(20);
            mainLayout.setStyle("-fx-padding: 20; -fx-background-color: #ffffff;");

            DoubleProperty totalPrice = new SimpleDoubleProperty(0);

            for (DishesEntity dish : dishes) {
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
                    int newQty = currentQty + 1;
                    quantityText.setText(String.valueOf(newQty));
                    totalPrice.set(totalPrice.get() + dish.getPrice());

                    if (!selectedDishes.contains(dish)) {
                        selectedDishes.add(dish);
                    }
                });

                minusButton.setOnAction(e -> {
                    int currentQty = Integer.parseInt(quantityText.getText());
                    if (currentQty > 0) {
                        int newQty = currentQty - 1;
                        quantityText.setText(String.valueOf(newQty));
                        totalPrice.set(totalPrice.get() - dish.getPrice());

                        if (newQty == 0) {
                            selectedDishes.remove(dish);
                        }
                    }
                });

                quantityBox.getChildren().addAll(minusButton, quantityText, plusButton);

                dishCard.getChildren().addAll(dishName, dishDescription, dishPrice, quantityBox);
                mainLayout.getChildren().add(dishCard);
            }

            Button createOrderButton = new Button("Create Order");
            createOrderButton.setStyle("-fx-font-size: 14px;");
            createOrderButton.setOnAction(e -> createOrder(totalPrice.get(), selectedTable, selectedDishes));

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-font-size: 14px;");
            closeButton.setOnAction(event -> detailStage.close());


            Text totalText = new Text();
            totalText.textProperty().bind(Bindings.concat("Total price: ", totalPrice.asString(), " €"));
            totalText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            mainLayout.getChildren().addAll(closeButton, createOrderButton, totalText);

            Scene detailScene = new Scene(mainLayout, 400, 500);
            detailStage.setScene(detailScene);
            detailStage.show();
        }
    }

}