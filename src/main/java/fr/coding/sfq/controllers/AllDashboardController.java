package fr.coding.sfq.controllers;

import fr.coding.sfq.models.OrderDishiesEntity;
import fr.coding.sfq.models.OrdersEntity;
import fr.coding.sfq.models.TablesEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.Comparator;
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
    private HBox mine;

    @FXML
    private VBox inProgressOrdersList;
    @FXML
    private VBox finishedOrdersList;
    @FXML
    private Label inProgressTotalLabel;
    @FXML
    private Label finishedTotalLabel;

    ObservableList<TablesEntity> tableOrder = FXCollections.observableArrayList();
    ObservableList<TablesEntity> tables = FXCollections.observableArrayList();
    ObservableList<OrdersEntity> orders = FXCollections.observableArrayList();


    @FXML private VBox inProgressOrdersList;
    @FXML private VBox finishedOrdersList;
    @FXML private Label inProgressTotalLabel;
    @FXML private Label finishedTotalLabel;

    public void initialize() {
        Platform.runLater(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tableOrder = FXCollections.observableArrayList(session.createQuery("FROM TablesEntity o JOIN OrdersEntity t ON t.id = o.order.id", TablesEntity.class).stream().collect(Collectors.toList()));
                orders = FXCollections.observableArrayList(session.createQuery("FROM OrdersEntity o", OrdersEntity.class).stream().collect(Collectors.toList()));
                tables = FXCollections.observableArrayList(session.createQuery("FROM TablesEntity ", TablesEntity.class).stream().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            loadOrders();
            loadLastClient();
            loadRecentOrders();
            loadLastOrder();
            loadTableStatuses();
            loadRecentOrders();
        });
    }

    private void loadOrders() {
        TableNo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));

        OrderID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrder().getId()).asObject());
        Status.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isOccupied()));

        ordersTable.setItems(tableOrder);
    }

    private void loadLastClient() {
        VBox box = new VBox();
        Label titre = new Label("Commande servie : ");

        box.getChildren().add(titre);
        tables.stream().filter(TablesEntity::isOccupied).filter(fn -> fn.getOrder().getStatus() == 1).limit(5).forEach(fn -> {

            Label label = new Label(fn.getLocation() + " : ");

            List<OrderDishiesEntity> orderDishies = FXCollections.observableArrayList();

            System.out.println(fn.getOrder().getId());

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                orderDishies = FXCollections.observableArrayList(session.createQuery("FROM OrderDishiesEntity o JOIN DishesEntity d ON d.id = o.dish.id WHERE o.order.id = :orderId", OrderDishiesEntity.class).setParameter("orderId", fn.getOrder().getId()).stream().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            box.getChildren().add(label);

            orderDishies.stream().map(OrderDishiesEntity::getDish).forEach(dish -> {
                Label ldish = new Label();
                ldish.setText("     - " + dish.getName());
                box.getChildren().add(ldish);
            });

            mine.getChildren().add(box);

        });
    }

    private void loadLastOrder() {
        VBox box = new VBox();
        Label titre = new Label("Commande à servir : ");
        box.getChildren().add(titre);
        orders.stream().filter(fn -> fn.getStatus == 0).sorted(Comparator.comparingInt(OrdersEntity::getId).reversed()).limit(5).forEach(fn -> {

            List<OrderDishiesEntity> orderDishies = FXCollections.observableArrayList();

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                orderDishies = FXCollections.observableArrayList(session.createQuery("FROM OrderDishiesEntity o JOIN DishesEntity d ON d.id = o.dish.id WHERE o.order.id = :orderId", OrderDishiesEntity.class).setParameter("orderId", fn.getId()).stream().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Label label = new Label(String.valueOf(fn.getId()));

            box.getChildren().add(label);

            orderDishies.stream().map(OrderDishiesEntity::getDish).forEach(dish -> {
                Label ldish = new Label();
                ldish.setText("     - " + dish.getName());
                box.getChildren().add(ldish);
            });

            mine.getChildren().add(box);
        });
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

    private void loadRecentOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate now = LocalDate.now();

            // recup commande dernieres 24h
            List<OrdersEntity> inProgressOrders = session.createQuery("FROM OrdersEntity", OrdersEntity.class)
                    .stream()
                    .filter(o -> o.getDate().toInstant().isAfter(java.sql.Timestamp.valueOf(now.minusDays(1).atStartOfDay()).toInstant()) && (o.getStatus() == 0))
                    .collect(Collectors.toList());

            List<OrdersEntity> finishedOrders = session.createQuery("FROM OrdersEntity", OrdersEntity.class)
                    .stream()
                    .filter(o -> o.getDate().toInstant().isAfter(java.sql.Timestamp.valueOf(now.minusDays(1).atStartOfDay()).toInstant()) && (o.getStatus() == 1))
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

            if (inProgressOrdersList.getChildren().isEmpty()){
                inProgressTotalLabel.setText("Aucune commande en cours");
            } else {
                inProgressTotalLabel.setText("Total : " + totalInProgress + " €");
            }

            if (finishedOrdersList.getChildren().isEmpty()){
                finishedTotalLabel.setText("Aucune commande en cours");
            } else {
                finishedTotalLabel.setText("Total : " + totalFinished + " €");
            }

        }
    }
}

