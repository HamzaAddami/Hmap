package dev.hmap.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataBaseConfig {

    private static final String PROPERTIES_FILE = "application.properties";
    private static Properties properties;

    static{
        properties = new Properties();
        try (InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)){

            if(input == null){
                throw new RuntimeException(PROPERTIES_FILE + "NOT FOUND");
            }

            properties.load(input);

        }catch (IOException e){
            throw new RuntimeException("ERROR LOADING DATABASE CONFIGURATION" + e);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

}
