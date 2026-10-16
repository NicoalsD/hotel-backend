package com.hotel.reservations.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private CorsConfiguration apiCorsConfiguration(String origins) {
        CorsRegistry registry = new CorsRegistry();
        new CorsConfig(origins).addCorsMappings(registry);
        return registry.getCorsConfigurations().get("/api/**");
    }

    @Test
    void registraOrigenesDePatronYMetodosParaLaApi() {
        CorsConfiguration cors = apiCorsConfiguration("http://localhost:5173,https://*.vercel.app");

        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOriginPatterns()).containsExactly("http://localhost:5173", "https://*.vercel.app");
        assertThat(cors.getAllowedMethods()).containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @Test
    void ignoraEspaciosYEntradasVacias() {
        CorsConfiguration cors = apiCorsConfiguration(" http://localhost:5173 , ,");

        assertThat(cors.getAllowedOriginPatterns()).containsExactly("http://localhost:5173");
    }
}
