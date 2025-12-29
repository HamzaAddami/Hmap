package dev.hmap.config;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class ScanRepository {
    private static final EntityManagerFactory emf;

    static {
        Map<String, String> dbProps = new HashMap<>();
        dbProps.put("jakarta.persistence.jdbc.url", DataBaseConfig.getProperty("db.url"));
        dbProps.put("jakarta.persistence.jdbc.user", DataBaseConfig.getProperty("db.user"));
        dbProps.put("jakarta.persistence.jdbc.password", DataBaseConfig.getProperty("db.password"));

        emf = Persistence.createEntityManagerFactory("hmap-pu", dbProps);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}