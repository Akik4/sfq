package fr.coding.sfq.controllers;

import fr.coding.sfq.models.EmployeeEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EmployeeController {

    @FXML private TableView<EmployeeEntity> employeeTable;
    @FXML private TableColumn<EmployeeEntity, String> employeeNameColumn;
    @FXML private TableColumn<EmployeeEntity, String> employeePositionColumn;
    @FXML private TableColumn<EmployeeEntity, Number> employeeWorkHoursColumn;
    @FXML private TableColumn<EmployeeEntity, Number> employeeAgeColumn;
    @FXML private TableColumn<EmployeeEntity, Void> actionsColumn;

    @FXML private TextField employeeNameField;
    @FXML private TextField employeePositionField;
    @FXML private TextField employeeAgeField;
    @FXML private Button createEmployeeButton;

    @FXML private Button homeButton;

    private List<EmployeeEntity> employees = FXCollections.observableArrayList();

    /**
     * Initializes the controller and configures the user interface.
     * This method:
     * - Configures table columns
     * - Initializes event listeners
     * - Loads employees from the database
     */
    @FXML
    public void initialize() {
        employeeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        employeePositionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPosition()));
        employeeWorkHoursColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getHoursWorked()));
        employeeAgeColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAge()));

        createEmployeeButton.setOnAction(event -> createEmployee());

        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));

        addActionsToTable();

        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            employees = session.createQuery("From EmployeeEntity", EmployeeEntity.class).list();

            employees.stream().forEach(employee -> employeeTable.getItems().add(employee));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Creates a new employee.
     * 
     * @param name The name of the employee
     * @param position The position of the employee
     * @param age The age of the employee
     */
    private void createEmployee() {
        String employeeName;
        String employeePosition;
        String employeeAge;

        try {
            employeeName = employeeNameField.getText();
            employeePosition = employeePositionField.getText();
            employeeAge = employeeAgeField.getText();

            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                EmployeeEntity newEmployee = new EmployeeEntity(employeeName, 0, employeePosition, Integer.parseInt(employeeAge));

                // add to db
                session.save(newEmployee);
                transaction.commit();

                // temporary add to avoid calling db
                try {
                    employeeTable.getItems().add(newEmployee);
                    employeeTable.refresh();
                    System.out.println("ca passe askip");
                } catch (Exception e) {
                    System.out.println("erreur mpon copain");
                }


                // reset inputs
                employeeNameField.clear();
                employeePositionField.clear();
                employeeAgeField.clear();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Informations invalides");
            return;
        }

    }

    /**
     * Adds work hours to an employee.
     * 
     * @param employee The employee to update
     * @param hours The number of hours to add
     */
    private void addWorkHours(EmployeeEntity employee, double hours) {
        // Implementation of adding work hours to an employee
    }

    private void addActionsToTable() {
        Callback<TableColumn<EmployeeEntity, Void>, TableCell<EmployeeEntity, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<EmployeeEntity, Void> call(final TableColumn<EmployeeEntity, Void> param) {
                return new TableCell<>() {

                    private final Button addHoursBtn = new Button("Ajouter Heures");
                    private final Button deleteBtn = new Button("Virer");
                    private final HBox pane = new HBox(5, addHoursBtn, deleteBtn);

                    {
                        addHoursBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        addHoursBtn.setOnAction(event -> {
                            EmployeeEntity employee = getTableView().getItems().get(getIndex());

                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Ajouter des heures");
                            dialog.setHeaderText("Ajouter des heures pour : " + employee.getName());
                            dialog.setContentText("Heures à ajouter :");

                            dialog.showAndWait().ifPresent(input -> {
                                try (Session session = HibernateUtil.getSessionFactory().openSession()){
                                    Transaction transaction = session.beginTransaction();

                                    double additionalHours = Double.parseDouble(input);
                                    employee.setHoursWorked(employee.getHoursWorked() + additionalHours);
                                    session.update(employee);
                                    transaction.commit();

                                    //temporary update hours
                                    employeeTable.refresh();
                                } catch (NumberFormatException e) {
                                    System.out.println("Entrée invalide : doit être un nombre.");
                                }
                            });
                        });

                        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        deleteBtn.setOnAction(event -> {
                            EmployeeEntity employee = getTableView().getItems().get(getIndex());

                            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                                Transaction transaction = session.beginTransaction();

                                session.delete(employee);
                                transaction.commit();

                                // temporary remove
                                employeeTable.getItems().remove(employee);
                                employeeTable.refresh();
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }


}