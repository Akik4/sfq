package fr.coding.sfq.controllers;

import fr.coding.sfq.models.DishesEntity;
import fr.coding.sfq.util.HibernateUtil;
import fr.coding.sfq.models.*;
import fr.coding.sfq.util.HibernateUtil;
import fr.coding.sfq.util.OrderDetailsPopupUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TablesController {

    @FXML private TableView<TablesEntity> tablesTable;
    @FXML private TableColumn<TablesEntity, String> tableNumberColumn;
    @FXML private TableColumn<TablesEntity, String> statusColumn;
    @FXML private TableColumn<TablesEntity, String> assignedOrderColumn;
    @FXML private TableColumn<TablesEntity, String> statusOrderColumn;

    @FXML private Button markAvailableButton;
    @FXML private Button markOccupiedButton;
    @FXML private Button assignOrderButton;
    @FXML private TextField tableNumberField;
    @FXML private Button createTableButton;



    @FXML private Button homeButton;

    private ObservableList<TablesEntity> tables = FXCollections.observableArrayList();
    private List<DishesEntity> dishes = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        tableNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        statusColumn.setCellValueFactory(cellData -> {
                TablesEntity table = cellData.getValue();
                return new SimpleStringProperty(table.isOccupied() ? "Occupé" : "Disponible");
        });
        assignedOrderColumn.setCellValueFactory(cellData -> {
            OrdersEntity order = cellData.getValue().getOrder();
            return new SimpleStringProperty(order != null ? "Commande #" + order.getId() : "Aucune");
        });
        statusOrderColumn.setCellValueFactory(cellData -> {
            OrdersEntity order = cellData.getValue().getOrder();
            return new SimpleStringProperty(order != null ? (order.getStatus() ? "Livré" : "En cours") : "...");
        });
        tablesTable.setItems(tables);

        tablesTable.setRowFactory(tv -> {
            TableRow<TablesEntity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TablesEntity selectedTable = row.getItem();
                    OrdersEntity order = selectedTable.getOrder();

                    if (order != null) {
                        OrderDetailsPopupUtil.show(order);
                    }
                }
            });
            return row;
        });

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

    private static void isOccuped(TablesEntity selectedTable, boolean bOccupied) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            selectedTable.setOccupied(bOccupied);
            session.update(selectedTable);
            tx.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void markTableAvailable() {
        TablesEntity selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setOccupied(false);
            tablesTable.refresh();

            isOccuped(selectedTable, false);
            if (selectedTable.getOrder() != null) {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction tx = session.beginTransaction();
                    selectedTable.setOrder(null);
                    session.update(selectedTable);
                    tx.commit();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void markTableOccupied() {
        TablesEntity selectedTable = tablesTable.getSelectionModel().getSelectedItem();
        if (selectedTable != null) {
            selectedTable.setOccupied(true);
            tablesTable.refresh();

            isOccuped(selectedTable, true);
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
        OrdersEntity newOrder = new OrdersEntity(new Date(), false, totalPrice, table);
        Transaction tx = null;

        System.out.println(selectedDishes.size());

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
            initialize();
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

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                dishes = session.createQuery("FROM DishesEntity", DishesEntity.class).list();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            Stage detailStage = new Stage();
            detailStage.setTitle("Détails de la commande");

            DoubleProperty totalPrice = new SimpleDoubleProperty(0);

            // --- Zone scrollable (cards) ---
            VBox scrollableDishLayout = new VBox(20);
            scrollableDishLayout.setStyle("-fx-padding: 10;");
            scrollableDishLayout.setFillWidth(true);


            dishes.stream().forEach(dish -> {
                VBox dishCard = createDishCard(dish, totalPrice, selectedDishes);
                scrollableDishLayout.getChildren().add(dishCard);
            });

            // ScrollPane
            ScrollPane scrollPane = new ScrollPane(scrollableDishLayout);
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // searchBar
            TextField searchField = new TextField();
            searchField.setPromptText("Rechercher un plat...");
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                String lowerInput = newValue.toLowerCase().trim();
                scrollableDishLayout.getChildren().clear();
                dishes.stream()
                        .filter(dish -> dish.getDescription().toLowerCase().contains(lowerInput) || dish.getName().toLowerCase().contains(lowerInput))
                        .forEach(dish -> {
                            VBox dishCard = createDishCard(dish, totalPrice, selectedDishes);
                            scrollableDishLayout.getChildren().add(dishCard);
                        });
            });

            // --- total + boutons ---
            Text totalText = new Text();
            totalText.textProperty().bind(Bindings.concat("Total price: ", totalPrice.asString(), " €"));
            totalText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Button createOrderButton = new Button("Create Order");
            createOrderButton.setStyle("-fx-font-size: 14px;");
            createOrderButton.setOnAction(e -> {
                createOrder(totalPrice.get(), selectedTable, selectedDishes);
                detailStage.close();
            });

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-font-size: 14px;");
            closeButton.setOnAction(event -> detailStage.close());

            HBox buttonBox = new HBox(10, createOrderButton, closeButton);
            buttonBox.setAlignment(Pos.CENTER);

            VBox bottomBox = new VBox(10, totalText, buttonBox);
            bottomBox.setStyle("-fx-padding: 10; -fx-background-color: #ffffff;");
            bottomBox.setAlignment(Pos.CENTER);

            // --- Layout principal ---
            BorderPane mainLayout = new BorderPane();
            mainLayout.setTop(searchField);
            mainLayout.setCenter(scrollPane);
            mainLayout.setBottom(bottomBox);

            // --- Scene ---
            Scene detailScene = new Scene(mainLayout, 400, 500);
            detailStage.setScene(detailScene);
            detailStage.show();

        }

    }

    private VBox createDishCard(DishesEntity dish, DoubleProperty totalPrice, List<DishesEntity> selectedDishes) {
        VBox dishCard = new VBox(10);
        dishCard.setStyle("-fx-padding: 15; -fx-border-color: black; -fx-background-color: white;");

        Text dishName = new Text(dish.getName());
        dishName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Text dishDescription = new Text(dish.getDescription());
        dishDescription.setStyle("-fx-font-size: 14px;");

        Text dishPrice = new Text(dish.getPrice() + " €");
        dishPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #28a745;");

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
            selectedDishes.add(dish);
        });

        minusButton.setOnAction(e -> {
            int currentQty = Integer.parseInt(quantityText.getText());
            if (currentQty > 0) {
                int newQty = currentQty - 1;
                quantityText.setText(String.valueOf(newQty));
                totalPrice.set(totalPrice.get() - dish.getPrice());
                if (newQty == 0) selectedDishes.remove(dish);
            }
        });

        quantityBox.getChildren().addAll(minusButton, quantityText, plusButton);
        dishCard.getChildren().addAll(dishName, dishDescription, dishPrice, quantityBox);

        return dishCard;
    }
}