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

import java.util.List;
import java.util.stream.Collectors;

public class AllDashboardController {

    @FXML private TableColumn<TablesEntity, Integer> OrderID;
    @FXML private TableColumn<TablesEntity, String> TableNo;
    @FXML private TableColumn<TablesEntity, Boolean> Status;

    @FXML private TableView<TablesEntity> ordersTable;
    @FXML private GridPane tableGrid;

    @FXML private VBox mine;

    ObservableList<TablesEntity> orders = FXCollections.observableArrayList();
    ObservableList<TablesEntity> tables = FXCollections.observableArrayList();

    public void initialize() {
        Platform.runLater(() -> {
            try(Session session = HibernateUtil.getSessionFactory().openSession()){
                orders = FXCollections.observableArrayList(session.createQuery("FROM TablesEntity o JOIN OrdersEntity t ON t.id = o.order.id", TablesEntity.class).stream().collect(Collectors.toList()));
                tables = FXCollections.observableArrayList(session.createQuery("FROM TablesEntity ", TablesEntity.class).stream().collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            loadOrders();
            loadTableStatuses();
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
//                tableGrid.add(new Text("test"), 1, 1);
                }
        );
    }
}