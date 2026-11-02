package com.crowdar.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lee config.properties desde el classpath. Una propiedad de sistema (-Dclave=valor)
 * tiene prioridad sobre el valor del archivo.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("No se encontró " + CONFIG_FILE + " en el classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + CONFIG_FILE, e);
        }
        return properties;
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String fileValue = PROPERTIES.getProperty(key);
        if (fileValue == null) {
            throw new IllegalArgumentException("Propiedad de configuración inexistente: " + key);
        }
        return fileValue.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
