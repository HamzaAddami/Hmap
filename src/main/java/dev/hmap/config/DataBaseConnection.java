package dev.hmap.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.hmap.exception.DatabaseConnectionException;
import java.sql.Connection;
import java.sql.SQLException;


public class DataBaseConnection {

    private static DataBaseConnection instance;
    private final HikariDataSource dataSource;

    private DataBaseConnection() {
        try{
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DataBaseConfig.getUrl());
            config.setUsername(DataBaseConfig.getUsername());
            config.setPassword(DataBaseConfig.getPassword());

            dataSource = new HikariDataSource(config);

        }catch (Exception e){
            throw new DatabaseConnectionException("Failed to initialize connection pool", e);
        }
    }

    public static synchronized DataBaseConnection getInstance(){
        if(instance == null){
            instance = new DataBaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✓ Database connection pool closed");
        }
    }

}
