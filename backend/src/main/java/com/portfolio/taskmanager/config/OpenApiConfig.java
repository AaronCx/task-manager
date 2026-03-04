package com.portfolio.taskmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger UI configuration.
 *
 * The JWT Bearer security scheme is registered globally so every
 * protected endpoint shows the "Authorize" padlock in Swagger UI.
 *
 * Access the UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Task Manager API",
        version     = "1.0",
        description = "Full-stack portfolio project — Spring Boot 3 + JWT + PostgreSQL",
        contact     = @Contact(name = "Portfolio", url = "https://github.com/AaronCx")
    ),
    servers = @Server(url = "/", description = "Default server"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name         = "bearerAuth",
    type         = SecuritySchemeType.HTTP,
    scheme       = "bearer",
    bearerFormat = "JWT",
    in           = SecuritySchemeIn.HEADER,
    description  = "Paste the JWT returned by /api/auth/login into this field."
)
public class OpenApiConfig {
    // Configuration is driven entirely by annotations above.
}
