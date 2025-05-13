package fr.coding.sfq;

import fr.coding.sfq.models.TransactionEntity;
import fr.coding.sfq.util.HibernateUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionsController {

    // Liens avec FXML
    @FXML private TableView<TransactionEntity> transactionsTable;
    @FXML private TableColumn<TransactionEntity, String> typeColumn;
    @FXML private TableColumn<TransactionEntity, String> dateColumn;
    @FXML private TableColumn<TransactionEntity, Number> amountColumn;
    @FXML private TableColumn<TransactionEntity, String> descriptionColumn;
    @FXML private Button homeButton;
    @FXML private Button downloadPdfButton;

    // Liste observable + format de date
    private final ObservableList<TransactionEntity> transactions = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));
        downloadPdfButton.setOnAction(event -> generatePdf());

        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getTypeLabel(cellData.getValue().getType())));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp().format(formatter)));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        // Lier à la table
        transactionsTable.setItems(transactions);

        // Fonction qui Select toute la BDD et qui Add a la liste qui affiche les transactions
        loadTransactionsFromDatabase();

        addIncome(120.0, "Paiement table 1");
        addExpense(45.0, "Salaire employé");
        addIncome(90.0, "Paiement table 2");
    }

    private void addIncome(double amount, String description) {
        TransactionEntity transaction = new TransactionEntity(LocalDateTime.now(), TransactionEntity.Type.INCOME, amount, description);
        saveTransaction(transaction);
    }

    private void addExpense(double amount, String description) {
        TransactionEntity transaction = new TransactionEntity(LocalDateTime.now(), TransactionEntity.Type.EXPENSE, amount, description);
        saveTransaction(transaction);
    }

    // Fonction qui attend une autre fonction pour add Expense or Income dans la BDD
    private void saveTransaction(TransactionEntity transaction) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(transaction);
            tx.commit();
            transactions.add(transaction);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Fonction qui Select toute la BDD et qui Add a la liste qui affiche les transactions
    private void loadTransactionsFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<TransactionEntity> result = session.createQuery("FROM TransactionEntity", TransactionEntity.class).list();
            transactions.setAll(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionEntity.Type.INCOME)
                .mapToDouble(TransactionEntity::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionEntity.Type.EXPENSE)
                .mapToDouble(TransactionEntity::getAmount)
                .sum();
    }

    // pour transformer Income et Expense en Recette et Depense pour le visuel en Francais dans le front
    private String getTypeLabel(TransactionEntity.Type type) {
        return switch (type) {
            case INCOME -> "Recette";
            case EXPENSE -> "Dépense";
        };
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

            document.add(new Paragraph("Historique des transactions\n\n"));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            table.addCell("Type");
            table.addCell("Date");
            table.addCell("Montant (€)");
            table.addCell("Description");

            for (TransactionEntity t : transactions) {
                table.addCell(getTypeLabel(t.getType()));
                table.addCell(t.getTimestamp().format(formatter));
                table.addCell(String.format("%.2f", t.getAmount()));
                table.addCell(t.getDescription());
            }

            document.add(table);
            document.close();
            System.out.println("PDF généré : " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
