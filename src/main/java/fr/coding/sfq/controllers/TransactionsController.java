package fr.coding.sfq.controllers;

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
import com.lowagie.text.pdf.PdfPCell;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionsController {

    // Link with Fxml
    @FXML private TableView<TransactionEntity> transactionsTable;
    @FXML private TableColumn<TransactionEntity, String> typeColumn;
    @FXML private TableColumn<TransactionEntity, String> dateColumn;
    @FXML private TableColumn<TransactionEntity, Number> amountColumn;
    @FXML private TableColumn<TransactionEntity, String> descriptionColumn;
    @FXML private Button homeButton;
    @FXML private Button downloadPdfButton;

    // List + format date
    private final ObservableList<TransactionEntity> transactions = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Initializes the controller and configures the user interface.
     * This method:
     * - Configures table columns
     * - Initializes event listeners
     * - Loads transactions from the database
     */
    @FXML
    public void initialize() {
        homeButton.setOnAction(event -> MainController.getInstance().switchView("HomePage.fxml"));
        downloadPdfButton.setOnAction(event -> generatePdf());

        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getTypeLabel(cellData.getValue().getType())));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp().format(formatter)));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        transactionsTable.setItems(transactions);

        // Function check bdd and add to the list for show on front
        loadTransactionsFromDatabase();

    }

    /**
     * Saves a transaction to the database.
     * 
     * @param transaction The transaction to save
     */
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

    /**
     * Loads transactions from the database.
     * This method retrieves all transactions and displays them in the table.
     * 
     * @return List of all transactions from the database
     */
    private void loadTransactionsFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<TransactionEntity> result = session.createQuery("FROM TransactionEntity", TransactionEntity.class).list();
            transactions.setAll(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // switch for show Recette or Depense in front
    private String getTypeLabel(TransactionEntity.Type type) {
        return switch (type) {
            case INCOME -> "Recette";
            case EXPENSE -> "Dépense";
        };
    }

    /**
     * Generates a PDF containing the transaction history.
     * The PDF includes:
     * - A table of transactions
     * - Income and expense totals
     * - Final balance
     * 
     * @param file The file where to save the PDF
     */
    private void generatePdf() {
        // open pop up for save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF des transactions");
        fileChooser.setInitialFileName("transactions.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));


        File file = fileChooser.showSaveDialog(transactionsTable.getScene().getWindow());
        if (file == null) return; // if user leave we close the pop up

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // add title for the pdf
            document.add(new Paragraph("Historique des transactions\n\n"));

            // create table with 4 colmun
            PdfPTable table = new PdfPTable(4);
            // Style table
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // headers for table
            String[] headers = {"Type", "Date", "Montant (€)", "Description"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header));
                headerCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                headerCell.setPadding(10f);
                table.addCell(headerCell);
            }

            // Variable to count the result of transactions
            double totalIncome = 0;
            double totalExpense = 0;

            for (TransactionEntity t : transactions) {
                PdfPCell typeCell = new PdfPCell(new Paragraph(getTypeLabel(t.getType())));
                typeCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                typeCell.setPadding(8f);
                table.addCell(typeCell);

                PdfPCell dateCell = new PdfPCell(new Paragraph(t.getTimestamp().format(formatter)));
                dateCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                dateCell.setPadding(8f);
                table.addCell(dateCell);

                // Cell Amount different because color added depending on Income or Expense + amount added to the variable at the start
                Paragraph amountPara = new Paragraph(String.format("%.2f €", t.getAmount()));
                if (t.getType() == TransactionEntity.Type.INCOME) {
                    amountPara.getFont().setColor(0, 128, 0);
                    totalIncome += t.getAmount();
                } else {
                    amountPara.getFont().setColor(200, 0, 0);
                    totalExpense += t.getAmount();
                }
                PdfPCell amountCell = new PdfPCell(amountPara);
                amountCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                amountCell.setPadding(8f);
                table.addCell(amountCell);

                PdfPCell descCell = new PdfPCell(new Paragraph(t.getDescription()));
                descCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                descCell.setPadding(8f);
                table.addCell(descCell);
            }

            // add table on pdf
            document.add(table);
            document.add(new Paragraph("\nRécapitulatif :\n"));

            // line "Total Recettes"
            Paragraph incomeLine = new Paragraph();
            com.lowagie.text.Font greenFont = new com.lowagie.text.Font();
            greenFont.setColor(0, 128, 0);
            incomeLine.add(new com.lowagie.text.Chunk("Total ", greenFont));
            incomeLine.add(new com.lowagie.text.Chunk("Recettes", greenFont));
            incomeLine.add(new com.lowagie.text.Chunk(String.format(" : %.2f €", totalIncome), greenFont));
            document.add(incomeLine);

            // line "Total Dépenses"
            Paragraph expenseLine = new Paragraph();
            com.lowagie.text.Font redFont = new com.lowagie.text.Font();
            redFont.setColor(200, 0, 0);
            expenseLine.add(new com.lowagie.text.Chunk("Total ", redFont));
            expenseLine.add(new com.lowagie.text.Chunk("Dépenses", redFont));
            expenseLine.add(new com.lowagie.text.Chunk(String.format(" : %.2f €", totalExpense), redFont));
            document.add(expenseLine);

            // Calculation + display of the result
            double balance = totalIncome - totalExpense;
            Paragraph balanceLine = new Paragraph("Solde Final : " + String.format("%.2f €", balance));
            balanceLine.setSpacingBefore(5f);
            document.add(balanceLine);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
