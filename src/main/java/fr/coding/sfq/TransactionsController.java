package fr.coding.sfq;

import fr.coding.sfq.models.Transaction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionsController {

    // Creation Variable relier au FXML
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, Number> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;

    // Liste Transaction + Format Date
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().toString()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp().format(formatter)));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        transactionsTable.setItems(transactions);

        addIncome(120.0, "Payment from table 1");
        addExpense(45.0, "Employee salary");
        addIncome(90.0, "Payment from table 2");
    }

    private void addIncome(double amount, String description) {
        Transaction transaction = new Transaction(LocalDateTime.now(), Transaction.Type.INCOME, amount, description);
        transactions.add(transaction);
    }

    private void addExpense(double amount, String description) {
        Transaction transaction = new Transaction(LocalDateTime.now(), Transaction.Type.EXPENSE, amount, description);
        transactions.add(transaction);
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }
}
