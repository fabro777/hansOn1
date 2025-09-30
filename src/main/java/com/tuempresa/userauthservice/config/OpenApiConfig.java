package com.tuempresa.userauthservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configuración de OpenAPI para documentar los endpoints de autenticación.
     * @return configuración de OpenAPI
     */
    @Bean
    public OpenAPI userAuthServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de desarrollo");

        Contact contact = new Contact();
        contact.setEmail("contacto@tuempresa.com");
        contact.setName("Equipo de Desarrollo");
        contact.setUrl("https://www.tuempresa.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("User Authentication Service API")
                .version("1.0.0")
                .contact(contact)
                .description("API simple para autenticación de usuarios con endpoints de registro, login y logout.")
                .termsOfService("https://www.tuempresa.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
