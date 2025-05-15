package fr.coding.sfq.controllers;
import fr.coding.sfq.util.TransactionsUtil;

import fr.coding.sfq.models.OrdersEntity;
import fr.coding.sfq.models.TablesEntity;
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
    @FXML private TableColumn<OrdersEntity, String> tableColumn;
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
        statusColumn.setCellValueFactory(cellData -> {
            int status = cellData.getValue().getStatus();
            String statusText = switch (status) {
                case 1 -> "Livré";
                case 2 -> "Payé";
                default -> "En cours";
            };
            return new SimpleStringProperty(statusText);
        });
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()));
        tableColumn.setCellValueFactory(cellData -> {
            TablesEntity table = cellData.getValue().getTable();

            return new SimpleStringProperty(table != null ? "Table: " + table.getLocation() : "Aucune");
        });
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

    // add button for different option on order
    private void addActionsToTable() {
        Callback<TableColumn<OrdersEntity, Void>, TableCell<OrdersEntity, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<OrdersEntity, Void> call(final TableColumn<OrdersEntity, Void> param) {
                return new TableCell<>() {
                    private final Button payButton = new Button("Payer");
                    private final Button validateButton = new Button("Valider");
                    private final Button cancelButton = new Button("Annuler");

                    private final HBox hBox = new HBox(5,payButton, validateButton, cancelButton);

                    {
                        // style button
                        payButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                        validateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        payButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

                        // action button who call different function define under
                        payButton.setOnAction(event -> {
                            OrdersEntity order = getTableView().getItems().get(getIndex());
                            payOrder(order);
                        });

                        validateButton.setOnAction(event -> {
                            OrdersEntity order = getTableView().getItems().get(getIndex());
                            validateOrder(order);
                        });

                        cancelButton.setOnAction(event -> {
                            OrdersEntity order = getTableView().getItems().get(getIndex());
                            cancelOrder(order);
                        });

                        payButton.setOnAction(event -> {
                            OrdersEntity order = getTableView().getItems().get(getIndex());
                            payOrder(order);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            OrdersEntity order = getTableView().getItems().get(getIndex());

                            HBox hBox = new HBox(5);

                            if (order.getStatus() == 0) {
                                hBox.getChildren().add(validateButton);
                                hBox.getChildren().add(cancelButton);
                            } else if (order.getStatus() == 1) {
                                hBox.getChildren().add(payButton);
                                hBox.getChildren().add(cancelButton);
                            }
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

            order.setStatus(1);
            session.update(order);
            tx.commit();
            orderTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
        TransactionsUtil.addExpense(order.getPriceProduction(), "Prix Production des plats de la commande ID: " + order.getId());
    }

    private void cancelOrder(OrdersEntity order) {

        // Step1
        Step1(order);

        // Step2
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.delete(order);
            tx.commit();
            orders.remove(order);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void Step1(OrdersEntity order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            TablesEntity tableWithOrder = getTable(order, session);

            tableWithOrder.setOrder(null);
            tableWithOrder.setOccupied(false);
            session.update(tableWithOrder);

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void payOrder(OrdersEntity order) {
        TransactionsUtil.addIncome(order.getPrice(), "Paiement de la commande ID: " + order.getId());
        Step1(order);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            order.setStatus(2);
            session.update(order);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize();
    }

    private static TablesEntity getTable(OrdersEntity order, Session session) {
        TablesEntity tablesWithOrder = session.createQuery(
                        "FROM TablesEntity WHERE order = :order", TablesEntity.class)
                .setParameter("order", order)
                .getSingleResult();
        return tablesWithOrder;
    }

}
