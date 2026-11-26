package com.fullcycle.admin.catalogo.infrastructure.configuration;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO(security): Configure OAuth2/JWT authentication, RBAC, rate limiting, and security headers (CSP, X-Content-Type-Options, X-Frame-Options, strict CORS) when Spring Security / Keycloak is integrated.
@AutoConfigurationPackage(basePackages = "com.fullcycle.admin.catalogo")
@SpringBootApplication(scanBasePackages = "com.fullcycle.admin.catalogo")
public class WebServerConfig {
}
