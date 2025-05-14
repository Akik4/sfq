package fr.coding.sfq.controllers;

import fr.coding.sfq.models.DishesEntity;
import fr.coding.sfq.models.EmployeeEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hibernate.Session;

import java.util.Comparator;
import java.util.List;

public class DashboardController {
    @FXML private Label under30Label;
    @FXML private Label between30And45Label;
    @FXML private Label over45Label;

    @FXML private Label mostExpensiveDishLabel;
    @FXML private Label cheapestDishLabel;
    @FXML private Label totalMenuValueLabel;

    @FXML
    public void initialize() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<EmployeeEntity> employees = session.createQuery("From EmployeeEntity", EmployeeEntity.class).list();
            List<DishesEntity> dishes = session.createQuery("From DishesEntity", DishesEntity.class).list();

            long under30 = employees.stream().filter(e -> e.getAge() < 30).count();
            long between30And45 = employees.stream().filter(e -> e.getAge() >= 30 && e.getAge() <= 45).count();
            long over45 = employees.stream().filter(e -> e.getAge() > 45).count();

            under30Label.setText("Employés < 30 ans : " + under30);
            between30And45Label.setText("Entre 30 et 45 ans : " + between30And45);
            over45Label.setText("Employés > 45 ans : " + over45);

            if (!dishes.isEmpty()) {
                DishesEntity mostExpensive = dishes.stream().max(Comparator.comparingDouble(DishesEntity::getPrice)).orElse(null);
                DishesEntity cheapest = dishes.stream().min(Comparator.comparingDouble(DishesEntity::getPrice)).orElse(null);
                double totalValue = dishes.stream().mapToDouble(DishesEntity::getPrice).sum();

                mostExpensiveDishLabel.setText("Plat le + cher : " + mostExpensive.getName() + " (" + mostExpensive.getPrice() + " €)");
                cheapestDishLabel.setText("Plat le – cher : " + cheapest.getName() + " (" + cheapest.getPrice() + " €)");
                totalMenuValueLabel.setText("Valeur totale de la carte : " + totalValue + " €");
            } else {
                mostExpensiveDishLabel.setText("Pas de plats enregistrés.");
                cheapestDishLabel.setText("");
                totalMenuValueLabel.setText("");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
