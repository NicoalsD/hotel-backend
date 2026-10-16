package com.hotel.reservations.dto;

import java.time.LocalDateTime;

/**
 * Cuerpo estándar de las respuestas de error de la API.
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path) {
}
