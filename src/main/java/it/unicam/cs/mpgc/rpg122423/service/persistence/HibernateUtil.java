package it.unicam.cs.mpgc.rpg122423.service.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Gestisce la creazione e il ciclo di vita della SessionFactory di Hibernate.
 * Non è più un Singleton statico: l'istanza viene creata e iniettata esplicitamente.
 */
public class HibernateUtil {
    private final SessionFactory sessionFactory;

    public HibernateUtil() {
        try {
            this.sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutdown() {
        sessionFactory.close();
    }
}
