package com.fullcycle.admin.catalogo.infrastructure;

import com.fullcycle.admin.catalogo.infrastructure.configuration.WebServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.AbstractEnvironment;

public class Main {
    public static void main(String[] args) {
        if (System.getProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME) == null) {
            System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "development");
        }
        SpringApplication.run(WebServerConfig.class, args);
    }
}
