package fr.coding.sfq;

import fr.coding.sfq.util.HibernateUtil;
import org.hibernate.Session;

public class HibernateTest {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("MariaDB Connection Successful!");
        } catch (Exception e) {
            System.out.println("Failed to connect: " + e.getMessage());
        }
    }
}