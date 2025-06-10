package full_framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
//    Loads properties from config file

    static private Properties properties;
    static final String CONFIG_FILE_PATH = "src/main/resources/config/config.properties";

    private ConfigLoader() {
        // Private constructor to prevent instantiation
    }

    public static void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading config.properties: " + e.getMessage());
                throw new RuntimeException("Failed to load configuration properties.", e);
            }
        }
    }

    public static String getStringProperty(String key) {
        if (properties == null)
            loadProperties(); // Ensure properties are loaded if not already

        String value = properties.getProperty(key);
        if (value == null)
            throw new RuntimeException("Property '" + key + "' not found in config.properties.");

        return value;
    }

}
