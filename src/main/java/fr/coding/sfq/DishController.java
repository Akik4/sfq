package fr.coding.sfq;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

    private void addDish() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        int price;

        try {
            price = Integer.parseInt(priceField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Prix invalide !");
            return; // Prevents invalid entry
        }

        // Create dish card
        VBox dishCard = new VBox(10);
        dishCard.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-background-color: #f8f8f8; -fx-border-radius: 5;");

        // Dish name
        Text dishName = new Text(name);
        dishName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Dish description
        Text dishDescription = new Text(description);
        dishDescription.setStyle("-fx-font-size: 14px;");

        // Dish price
        Text dishPrice = new Text(price + " €");
        dishPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");

        // Optional image (use placeholder for now)
        ImageView dishImage = new ImageView(new Image(getClass().getResource("/images/placeholder.png").toString()));
        dishImage.setFitWidth(120);
        dishImage.setFitHeight(120);

        dishCard.getChildren().addAll(dishImage, dishName, dishDescription, dishPrice);
        dishGrid.getChildren().add(dishCard);

        // Clear fields after adding
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
    }
}