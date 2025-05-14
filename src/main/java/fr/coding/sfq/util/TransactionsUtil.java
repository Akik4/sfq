package fr.coding.sfq.util;

import fr.coding.sfq.models.TransactionEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class TransactionsUtil {

    public static void addIncome(double amount, String description) {
        TransactionEntity transaction = new TransactionEntity(LocalDateTime.now(), TransactionEntity.Type.INCOME, amount, description);
        saveTransaction(transaction);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
        }
    }

    private static void saveTransaction(TransactionEntity transaction) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public static void addExpense(double amount, String description) {
        TransactionEntity transaction = new TransactionEntity(LocalDateTime.now(), TransactionEntity.Type.EXPENSE, amount, description);

        saveTransaction(transaction);
    }
}
