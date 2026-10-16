package com.hotel.reservations.dto;

import java.time.LocalDate;

/**
 * Solicitud para crear una reserva.
 */
public record ReservationRequestDTO(Long roomId, LocalDate checkIn, LocalDate checkOut, GuestDTO guest) {
}
