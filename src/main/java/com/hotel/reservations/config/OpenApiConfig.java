package com.hotel.reservations.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion OpenAPI (Swagger) de la API del hotel.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define la informacion general mostrada en Swagger UI.
     *
     * @return especificacion OpenAPI base de la aplicacion
     */
    @Bean
    public OpenAPI hotelOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API Sistema de Reservas de Hotel")
                .description("API para la gestion de reservas, check-in, check-out, servicios adicionales, "
                        + "tarifas y facturacion del hotel.")
                .version("1.0.0"));
    }
}
