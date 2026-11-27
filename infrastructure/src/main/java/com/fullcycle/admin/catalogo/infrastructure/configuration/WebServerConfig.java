package com.fullcycle.admin.catalogo.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO(security): Configure OAuth2/JWT authentication, RBAC, rate limiting, and security headers (CSP, X-Content-Type-Options, X-Frame-Options, strict CORS) when Spring Security / Keycloak is integrated.
@OpenAPIDefinition(
        info = @Info(
                title = "Admin do Catálogo GatoFlix",
                description = "Microsserviço de administração do catálogo de vídeos da plataforma GatoFlix",
                version = "1.0.0"
        )
)
@AutoConfigurationPackage(basePackages = "com.fullcycle.admin.catalogo")
@SpringBootApplication(scanBasePackages = "com.fullcycle.admin.catalogo")
public class WebServerConfig {
}
