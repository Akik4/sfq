package fr.coding.sfq;

import fr.coding.sfq.models.OrdersEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hibernate.Session;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrdersController {

    @FXML private TableView<OrdersEntity> orderTable;
    @FXML private TableColumn<OrdersEntity, String> dateColumn;
    @FXML private TableColumn<OrdersEntity, String> statusColumn;
    @FXML private TableColumn<OrdersEntity, Number> priceColumn;
    @FXML private TableColumn<OrdersEntity, Number> idColumn;
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
}
