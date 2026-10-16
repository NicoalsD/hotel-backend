package com.hotel.reservations.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Solicitud para crear una reserva.
 */
public record ReservationRequestDTO(
        @NotNull(message = "El identificador de la habitación es obligatorio") Long roomId,
        @NotNull(message = "La fecha de llegada es obligatoria") LocalDate checkIn,
        @NotNull(message = "La fecha de salida es obligatoria") LocalDate checkOut,
        GuestDTO guest) {
}
