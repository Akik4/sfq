package fr.coding.sfq.controllers;

import fr.coding.sfq.models.OrdersEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrdersController {

    @FXML private TableView<OrdersEntity> orderTable;
    @FXML private TableColumn<OrdersEntity, String> dateColumn;
    @FXML private TableColumn<OrdersEntity, String> statusColumn;
    @FXML private TableColumn<OrdersEntity, Number> priceColumn;
    @FXML private TableColumn<OrdersEntity, Number> idColumn;
    @FXML private TableColumn<OrdersEntity, Void> actionsColumn;
    @FXML private Button homeButton;

    private final ObservableList<OrdersEntity> orders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        homeButton.setOnAction(e -> MainController.getInstance().switchView("HomePage.fxml"));

        idColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getId()));
        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(formatter.format(cellData.getValue().getDate()));
        });
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus() ? "Terminée" : "En cours"));
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()));

        orderTable.setItems(orders);

        addActionsToTable();
        loadOrdersFromDatabase();
    }

    private void loadOrdersFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<OrdersEntity> result = session.createQuery("FROM OrdersEntity", OrdersEntity.class).list();
            orders.setAll(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addActionsToTable() {
        Callback<TableColumn<OrdersEntity, Void>, TableCell<OrdersEntity, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<OrdersEntity, Void> call(final TableColumn<OrdersEntity, Void> param) {
                return new TableCell<>() {
                    private final Button validateButton = new Button("Valider");
                    private final Button cancelButton = new Button("Annuler");
                    private final HBox hBox = new HBox(5, validateButton, cancelButton);

                    {
                        validateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                        validateButton.setOnAction(event -> {
                            OrdersEntity order = getTableView().getItems().get(getIndex());
                            validateOrder(order);
                        });

                        cancelButton.setOnAction(event -> {
                            OrdersEntity order = getTableView().getItems().get(getIndex());
                            cancelOrder(order);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void validateOrder(OrdersEntity order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            order.setStatus(true);
            session.update(order);
            tx.commit();
            orderTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelOrder(OrdersEntity order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(order);
            tx.commit();
            orders.remove(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
