package fr.coding.sfq.controllers;

import fr.coding.sfq.models.OrdersEntity;
import fr.coding.sfq.models.TablesEntity;
import fr.coding.sfq.util.HibernateUtil;
import jakarta.persistence.Column;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AllDashboardController {

    @FXML
    private TableColumn<TablesEntity, Integer> OrderID;
    @FXML
    private TableColumn<TablesEntity, String> TableNo;
    @FXML
    private TableColumn<TablesEntity, Boolean> Status;

    @FXML
    private TableView<TablesEntity> ordersTable;
    @FXML
    private GridPane tableGrid;

    @FXML
    private VBox mine;

    @FXML
    private VBox inProgressOrdersList;
    @FXML
    private VBox finishedOrdersList;
    @FXML
    private Label inProgressTotalLabel;
    @FXML
    private Label finishedTotalLabel;

    ObservableList<TablesEntity> orders = FXCollections.observableArrayList();
    ObservableList<TablesEntity> tables = FXCollections.observableArrayList();

    public void initialize() {
        Platform.runLater(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                orders = FXCollections.observableArrayList(session.createQuery("FROM TablesEntity o JOIN OrdersEntity t ON t.id = o.order.id", TablesEntity.class).stream().collect(Collectors.toList()));
                tables = FXCollections.observableArrayList(session.createQuery("FROM TablesEntity ", TablesEntity.class).stream().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            loadOrders();
            loadTableStatuses();
            loadRecentOrders();
        });
    }

    private void loadOrders() {
        TableNo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));

        OrderID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrder().getId()).asObject());
        Status.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isOccupied()));

        ordersTable.setItems(orders);
    }

    private void loadTableStatuses() {
        // Fetch table statuses and update tableStatusGrid
        System.out.println(orders.size());
        tables.stream().filter(TablesEntity::isOccupied).map(TablesEntity::getLocation).forEach(fn -> {
                    HBox box = new HBox();

                    box.getChildren().add(new Label(fn));

                    box.getChildren().add(new Circle(10, Color.GREEN));

                    mine.getChildren().add(box);
//                tableGrid.add(new Text("test"), 1, 1);
                }
        );

        tables.stream().filter(fn -> !fn.isOccupied()).map(TablesEntity::getLocation).forEach(fn -> {
            HBox box = new HBox();

            box.getChildren().add(new Label(fn));

            box.getChildren().add(new Circle(10, Color.RED));

            mine.getChildren().add(box);
        });
        orders.stream().map(TablesEntity::getLocation).forEach(fn -> {
                    mine.getChildren().add(new Label(fn));
//                tableGrid.add(new Text("test"), 1, 1);
                }
        );
    }

    private void loadRecentOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate now = LocalDate.now();

            // recup commande dernieres 24h
            List<OrdersEntity> inProgressOrders = session.createQuery("FROM OrdersEntity", OrdersEntity.class)
                    .stream()
                    .filter(o -> o.getDate().toInstant().isAfter(java.sql.Timestamp.valueOf(now.minusDays(1).atStartOfDay()).toInstant()) && !o.getStatus())
                    .collect(Collectors.toList());

            List<OrdersEntity> finishedOrders = session.createQuery("FROM OrdersEntity", OrdersEntity.class)
                    .stream()
                    .filter(o -> o.getDate().toInstant().isAfter(java.sql.Timestamp.valueOf(now.minusDays(1).atStartOfDay()).toInstant()) && o.getStatus())
                    .collect(Collectors.toList());


            inProgressOrdersList.getChildren().clear();
            finishedOrdersList.getChildren().clear();

            inProgressOrders.stream()
                    .map(order -> new Label("Commande #" + order.getId() + " - " + order.getPrice() + " €"))
                    .forEach(label -> inProgressOrdersList.getChildren().add(label));

            double totalInProgress = inProgressOrders.stream()
                    .mapToDouble(OrdersEntity::getPrice)
                    .sum();

            finishedOrders.stream()
                    .map(order -> new Label("Commande #" + order.getId() + " - " + order.getPrice() + " €"))
                    .forEach(label -> finishedOrdersList.getChildren().add(label));

            double totalFinished = finishedOrders.stream()
                    .mapToDouble(OrdersEntity::getPrice)
                    .sum();

            if (inProgressOrdersList.getChildren().isEmpty()) {
                inProgressTotalLabel.setText("Aucune commande en cours");
            } else {
                inProgressTotalLabel.setText("Total : " + totalInProgress + " €");
            }

            if (finishedOrdersList.getChildren().isEmpty()) {
                finishedTotalLabel.setText("Aucune commande en cours");
            } else {
                finishedTotalLabel.setText("Total : " + totalFinished + " €");
            }

        }
    }
}
