package com.hotel.reservations.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integracion del controlador con el contexto completo de Spring.
 */
@SpringBootTest
class HotelControllerIntegrationTest {

    private static final String VALID_RESERVATION = """
            {
              "roomId": 7,
              "checkIn": "2027-03-10",
              "checkOut": "2027-03-13",
              "guest": {
                "fullName": "Ana Pérez",
                "documentNumber": "1020304050",
                "email": "ana@correo.com",
                "phone": "3001234567"
              }
            }
            """;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void disponibilidadDevuelveLas16Habitaciones() throws Exception {
        mockMvc.perform(get("/api/hotel/disponibilidad")
                        .param("checkIn", "2027-03-10")
                        .param("checkOut", "2027-03-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(16));
    }

    @Test
    void reservarDevuelve201() throws Exception {
        mockMvc.perform(post("/api/hotel/reservar")
                        .contentType("application/json")
                        .content(VALID_RESERVATION))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void reservaDesconocidaDevuelve404() throws Exception {
        mockMvc.perform(get("/api/hotel/reserva/RES-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró la reserva RES-9999"));
    }

    @Test
    void reservaInvalidaDevuelve400() throws Exception {
        mockMvc.perform(post("/api/hotel/reservar")
                        .contentType("application/json")
                        .content("{\"roomId\": null, \"guest\": {\"email\": \"no-es-correo\"}}"))
                .andExpect(status().isBadRequest());
    }
}
