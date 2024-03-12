package app.connection;

import java.io.*;
import java.util.Properties;

public class DBConfig {
    private static final Properties properties = new Properties();
    private static final String DB_PROPERTIES = "db.properties";

    static {
        try (InputStream inputStream = DBConfig.class.getClassLoader().getResourceAsStream(DB_PROPERTIES)) {
            if (inputStream == null) {
                System.exit(1);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static String getURL() {
        return properties.getProperty("db.url");
    }

    static String getUsername() {
        return properties.getProperty("db.username");
    }

    static String getPassword() {
        return properties.getProperty("db.password");
    }

    static String getDriver() {
        return properties.getProperty("db.driver");
    }
}
