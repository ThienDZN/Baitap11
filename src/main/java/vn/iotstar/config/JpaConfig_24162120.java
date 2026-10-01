package vn.iotstar.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaConfig_24162120 {
    private static final String PERSISTENCE_UNIT = "jpa-hibernate-mysql";
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = buildEntityManagerFactory();

    private JpaConfig_24162120() {
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        Map<String, Object> overrides = new HashMap<>();
        applyOverride(overrides, "jakarta.persistence.jdbc.url", "spring.datasource.url", "APP_DB_URL");
        applyOverride(overrides, "jakarta.persistence.jdbc.user", "spring.datasource.username", "APP_DB_USERNAME");
        applyOverride(overrides, "jakarta.persistence.jdbc.password", "spring.datasource.password", "APP_DB_PASSWORD");
        applyOverride(overrides, "jakarta.persistence.jdbc.driver", "spring.datasource.driverClassName", "APP_DB_DRIVER");
        applyOverride(overrides, "hibernate.show_sql", "spring.jpa.show-sql", "APP_JPA_SHOW_SQL");
        applyOverride(overrides, "hibernate.format_sql", "spring.jpa.properties.hibernate.format_sql", "APP_JPA_FORMAT_SQL");
        applyOverride(overrides, "hibernate.hbm2ddl.auto", "spring.jpa.hibernate.ddl-auto", "APP_JPA_DDL_AUTO");
        applyOverride(overrides, "hibernate.dialect", "spring.jpa.properties.hibernate.dialect", "APP_JPA_DIALECT");
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, overrides);
    }

    private static void applyOverride(Map<String, Object> overrides, String propertyName,
                                      String applicationPropertyName, String envName) {
        String value = AppProperties_24162120.get(applicationPropertyName, null);
        if (value != null && !value.isBlank()) {
            overrides.put(propertyName, value.trim());
            return;
        }

        if (envName == null || envName.isBlank()) {
            return;
        }

        value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            overrides.put(propertyName, value.trim());
        }
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void close() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
