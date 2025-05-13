package fr.coding.sfq;

import fr.coding.sfq.models.Transaction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;


public class TransactionsController {

    // Creation Variable relier au FXML
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, Number> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private Button homeButton;
    @FXML private Button downloadPdfButton;

    // Liste Transaction + Format Date
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));
        downloadPdfButton.setOnAction(event -> generatePdf());


        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getTypeLabel(cellData.getValue().getType())));
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

    private String getTypeLabel(Transaction.Type type) {
        return switch (type) {
            case INCOME -> "Recette";
            case EXPENSE -> "Dépense";
        };
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }
    private void generatePdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF des transactions");
        fileChooser.setInitialFileName("transactions.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        File file = fileChooser.showSaveDialog(transactionsTable.getScene().getWindow());
        if (file == null) return;

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Titre
            document.add(new Paragraph("Historique des transactions\n\n"));

            // Création du tableau à 4 colonnes + design largeur etc
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // titre du tableau
            table.addCell("Type");
            table.addCell("Date");
            table.addCell("Montant (€)");
            table.addCell("Description");

            // Information des transactions
            for (Transaction t : transactions) {
                table.addCell(getTypeLabel(t.getType()));
                table.addCell(t.getTimestamp().format(formatter));
                table.addCell(String.format("%.2f", t.getAmount()));
                table.addCell(t.getDescription());
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
