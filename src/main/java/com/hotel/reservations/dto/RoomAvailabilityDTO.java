package com.hotel.reservations.dto;

import com.hotel.reservations.model.RoomType;

import java.math.BigDecimal;

/**
 * Disponibilidad de una habitación para un rango de fechas, con su costo estimado.
 */
public record RoomAvailabilityDTO(
        Long roomId,
        String number,
        RoomType type,
        String typeLabel,
        int floor,
        int capacity,
        BigDecimal basePrice,
        String description,
        boolean available,
        long nights,
        BigDecimal estimatedTotal) {
}
