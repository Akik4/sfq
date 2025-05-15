package fr.coding.sfq.util;

import fr.coding.sfq.models.DishesEntity;
import fr.coding.sfq.models.OrdersEntity;
import fr.coding.sfq.models.OrderDishiesEntity;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.util.List;

/**
 * Utility for displaying order details in a popup window.
 * This class manages the display of detailed order information, including:
 * - List of ordered dishes
 * - Dish descriptions and prices
 * - Total order price
 */
public class OrderDetailsPopupUtil {

    /**
     * Shows a popup window with order details.
     * 
     * @param order The order to display details for
     */
    public static void show(OrdersEntity order) {
        Stage detailStage = new Stage();
        detailStage.setTitle("Détails de la commande #" + order.getId());

        VBox dishList = new VBox(10);
        dishList.setPadding(new Insets(10));

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<OrderDishiesEntity> orderDishes = session
                    .createQuery("FROM OrderDishiesEntity WHERE order.id = :orderId", OrderDishiesEntity.class)
                    .setParameter("orderId", order.getId())
                    .list();

            for (OrderDishiesEntity od : orderDishes) {
                DishesEntity dish = od.getDish();

                VBox dishCard = new VBox(5);
                dishCard.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-background-color: white;");

                Text name = new Text(dish.getName());
                name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Text desc = new Text(dish.getDescription());
                desc.setStyle("-fx-font-size: 14px;");

                Text price = new Text(dish.getPrice() + " €");
                price.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

                dishCard.getChildren().addAll(name, desc, price);
                dishList.getChildren().add(dishCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(dishList);
        scrollPane.setFitToWidth(true);

        Text total = new Text("Prix total : " + order.getPrice() + " €");
        total.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox mainLayout = new VBox(10, scrollPane, total);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 400, 500);
        detailStage.setScene(scene);
        detailStage.show();
    }
}