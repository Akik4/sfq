package fr.coding.sfq.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utility for Hibernate configuration and session management.
 * This class provides a single instance of SessionFactory for the application.
 * It uses the Singleton pattern to ensure only one SessionFactory instance.
 */
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    /**
     * Gets the SessionFactory instance.
     * 
     * @return The configured SessionFactory instance
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
