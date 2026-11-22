package org.cryptochatbotxxx.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings from a .properties file on the classpath.
 */
public final class ConfigReader {

    private final Properties props = new Properties();

    // loads the config 
    public ConfigReader(String resourceName) {
        if (resourceName == null || resourceName.isBlank()) {
            throw new IllegalArgumentException("Le nom de la ressource est vide");
        }

        // Load from src/main/resources 
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalArgumentException("Fichier introuvable dans le classpath : " + resourceName);
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire " + resourceName, e);
        }
    }

    // returns value associated with key or defaultvalue if key is empty
    public String getString(String key, String defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("La clé ne peut pas être null");
        }
        String value = props.getProperty(key);
        // Avoid space issues in .properties values
        return value == null ? defaultValue : value.trim();
    }

    public int getInt(String key, int defaultValue) {
        String value = getString(key, null);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
           return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, null);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        // Boolean.parseBoolean() silently returns false on typos, so check explicitly
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Booléen invalide pour '" + key + "' : " + value);
    }
}