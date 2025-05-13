package fr.coding.sfq;

import fr.coding.sfq.models.Employee;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class EmployeeController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> employeeNameColumn;
    @FXML private TableColumn<Employee, String> employeePositionColumn;
    @FXML private TableColumn<Employee, Number> employeeWorkHoursColumn;
    @FXML private TableColumn<Employee, Number> employeeAgeColumn;
    @FXML private TableColumn<Employee, Void> actionsColumn;

    @FXML private TextField employeeNameField;
    @FXML private TextField employeePositionField;
    @FXML private TextField employeeAgeField;
    @FXML private Button createEmployeeButton;

    @FXML private Button homeButton;

    private ObservableList<Employee> employees = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        employeeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        employeePositionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPosition()));
        employeeWorkHoursColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getWorkHours()));
        employeeAgeColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAge()));
        employeeTable.setItems(employees);

        createEmployeeButton.setOnAction(event -> createTable());

        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));

        addActionsToTable();

        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }


    private void createTable() {
        String employeeName;
        String employeePostion;
        String employeeAge;

        try {
            employeeName = employeeNameField.getText();
            employeePostion = employeePositionField.getText();
            employeeAge = employeeAgeField.getText();
        } catch (NumberFormatException e) {
            System.out.println("Informations invalides");
            return;
        }

        Employee newEmployee = new Employee(employeeName, 0, employeePostion, Integer.parseInt(employeeAge));
        employees.add(newEmployee);
        employeeTable.refresh();

        employeeNameField.clear();
        employeePositionField.clear();
        employeeAgeField.clear();
    }

    private void addActionsToTable() {
        Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Employee, Void> call(final TableColumn<Employee, Void> param) {
                return new TableCell<>() {

                    private final Button addHoursBtn = new Button("Ajouter Heures");
                    private final Button deleteBtn = new Button("Virer");
                    private final HBox pane = new HBox(5, addHoursBtn, deleteBtn);

                    {
                        addHoursBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        addHoursBtn.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());

                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Ajouter des heures");
                            dialog.setHeaderText("Ajouter des heures pour : " + employee.getName());
                            dialog.setContentText("Heures à ajouter :");

                            dialog.showAndWait().ifPresent(input -> {
                                try {
                                    double additionalHours = Double.parseDouble(input);
                                    employee.setWorkHours(employee.getWorkHours() + additionalHours);
                                    employeeTable.refresh();
                                } catch (NumberFormatException e) {
                                    System.out.println("Entrée invalide : doit être un nombre.");
                                }
                            });
                        });

                        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        deleteBtn.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            employees.remove(employee);
                            employeeTable.refresh();
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