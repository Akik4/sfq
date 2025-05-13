package fr.coding.sfq;

import fr.coding.sfq.models.Dishes;
import fr.coding.sfq.models.DishesEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class DishController {

    @FXML private FlowPane dishGrid;
    @FXML private Button homeButton;
    @FXML private Button submitDishButton;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;

    @FXML
    public void initialize() {
        submitDishButton.setOnAction(event -> addDish());
        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));
    }

    private ObservableList<Dishes> dishes = FXCollections.observableArrayList();

    private void addDish() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        int price;

        try {
            price = Integer.parseInt(priceField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Prix invalide !");
            return;
        }

        // Create dish card dynamically
        VBox dishCard = new VBox(10);
        dishCard.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-background-color: #f8f8f8; -fx-border-radius: 5;");

        Text dishName = new Text(name);
        dishName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text dishDescription = new Text(description);
        dishDescription.setStyle("-fx-font-size: 14px;");

        Text dishPrice = new Text(price + " €");
        dishPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");

        ImageView dishImage = new ImageView(new Image("file:src/main/resources/images/placeholder.jpg"));
        dishImage.setFitWidth(120);
        dishImage.setFitHeight(120);

        dishCard.getChildren().addAll(dishImage, dishName, dishDescription, dishPrice);

        // Make dish card clickable for details
        dishCard.setOnMouseClicked(event -> {
            System.out.println("Event passed");
            showDishDetails(name);
        });

        dishGrid.getChildren().add(dishCard); // Add new dish card to the FlowPane

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            DishesEntity dish = new DishesEntity(name, description, price, "");
            session.save(dish);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dishes Dnew = new Dishes(name, description, price, "");
        dishes.add(Dnew);

        nameField.clear();
        descriptionField.clear();
        priceField.clear();
    }

    private void showDishDetails(String name) {
        System.out.println(dishes.size() + " dishes");
        List<Dishes> matchingDishes = dishes.stream()
                .filter(dish -> dish.getName().equals(name))
                .collect(Collectors.toList());

        System.out.println("Found dishes: " + matchingDishes.size());

        matchingDishes.stream().findFirst().ifPresent(dish -> {
            Stage detailStage = new Stage();
            detailStage.setTitle("Détails du Plat");

            VBox detailLayout = new VBox(15);
            detailLayout.setStyle("-fx-padding: 15; -fx-border-color: black; -fx-background-color: white;");

            Text dishName = new Text(dish.getName());
            dishName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

            Text dishDescription = new Text(dish.getDescription());
            dishDescription.setStyle("-fx-font-size: 16px;");

            Text dishPrice = new Text(dish.getPrice() + " €");
            dishPrice.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");

            Button closeButton = new Button("Fermer");
            closeButton.setOnAction(event -> detailStage.close());

            detailLayout.getChildren().addAll(dishName, dishDescription, dishPrice, closeButton);

            Scene detailScene = new Scene(detailLayout, 300, 200);
            detailStage.setScene(detailScene);
            detailStage.show();
        });
    }
}