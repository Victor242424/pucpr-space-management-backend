package dev.victor_rivas.space_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.dev-url:http://localhost:8081}")
    private String devUrl;

    @Value("${openapi.prod-url:https://api.spacemanagement.com}")
    private String prodUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        // Definir el esquema de seguridad JWT
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("Space Management System API")
                        .description("REST API for educational space management system. " +
                                "Allows managing students, spaces, access records and generating occupancy reports.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Victor Rivas")
                                .email("support@spacemanagement.com")
                                .url("https://github.com/victor-rivas-dev"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

                // Servidores disponibles
                .servers(List.of(
                        new Server()
                                .url(devUrl)
                                .description("Development Server"),
                        new Server()
                                .url(prodUrl)
                                .description("Production Server")
                ))

                // Configuración de seguridad JWT
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter the JWT token obtained from the /api/auth/login endpoint")));
    }
}