package com.qa.aem.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads settings from config/&lt;env&gt;.properties. A -D system property or an environment variable
 * with the same name overrides the file, which is how the username and password are supplied.
 */
public class ConfigReader {

    private static Properties properties;

    /** Environment chosen with -Denv=... (stage, dev or prod). Stage is the default. */
    public static String getEnvironment() {
        return System.getProperty("env", "stage");
    }

    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        if (value == null) {
            value = loadProperties().getProperty(key);
        }
        if (value == null) {
            throw new RuntimeException("No value found for '" + key + "'. Add it to config/"
                    + getEnvironment() + ".properties or set it as an environment variable.");
        }
        return value;
    }

    private static synchronized Properties loadProperties() {
        if (properties == null) {
            String fileName = "config/" + getEnvironment() + ".properties";
            try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(fileName)) {
                if (input == null) {
                    throw new RuntimeException("Config file not found: " + fileName);
                }
                properties = new Properties();
                properties.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Could not read " + fileName, e);
            }
        }
        return properties;
    }
}
