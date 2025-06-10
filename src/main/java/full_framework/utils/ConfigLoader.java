// src/full_framework/utils/ConfigLoader.java
package full_framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    static private Properties properties;
    static final String CONFIG_FILE_NAME = "config/config.properties";
    private static final Logger logger = LogManager.getLogger(ConfigLoader.class); // Logger for ConfigLoader

    private ConfigLoader() {
        // Private constructor to prevent instantiation
    }

    public static void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            logger.info("Attempting to load configuration properties from: " + CONFIG_FILE_NAME);
            try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
                if (inputStream == null) {
                    logger.fatal("Config file '" + CONFIG_FILE_NAME + "' not found on classpath. Ensure it exists in src/main/resources/config/.");
                    throw new RuntimeException("Config file '" + CONFIG_FILE_NAME + "' not found on classpath.");
                }
                properties.load(inputStream);
                logger.info("Configuration properties loaded successfully.");
            } catch (IOException e) {
                logger.fatal("Error loading config.properties: " + e.getMessage(), e);
                throw new RuntimeException("Failed to load configuration properties.", e);
            }
        }
    }

    public static String getStringProperty(String key) {
        if (properties == null) {
            loadProperties();
        }

        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Property '" + key + "' not found in config.properties. This may cause issues.");
            throw new RuntimeException("Property '" + key + "' not found in config.properties.");
        }
        logger.debug("Retrieved property: " + key + " = " + value);
        return value;
    }
}