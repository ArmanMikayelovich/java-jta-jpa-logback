package com.energizeglobal.internship.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public final class Properties {
    private Properties() {
    }

    private static final java.util.Properties PROPERTIES = init();

    private static java.util.Properties init() {
        try (InputStream fileInputStream = Properties.class.getClassLoader().getResourceAsStream("config.properties");
        ) {

            java.util.Properties prop = new java.util.Properties();
            prop.load(fileInputStream);
            log.debug("properties from file loaded.");
            return prop;

        } catch (IOException ex) {
            log.error("An error occurred in the property reading process: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static String get(String name) {
        return (String) PROPERTIES.get(name);
    }


}