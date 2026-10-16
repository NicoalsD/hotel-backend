package com.hotel.reservations.dto;

import java.time.LocalDateTime;

/**
 * Llave digital emitida al huésped durante el check-in.
 */
public record DigitalKeyDTO(String code, String roomNumber, LocalDateTime issuedAt, boolean active) {
}
