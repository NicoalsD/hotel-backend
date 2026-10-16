package com.hotel.reservations.dto;

import com.hotel.reservations.model.RoomStatus;
import com.hotel.reservations.model.RoomType;

import java.math.BigDecimal;

/**
 * Representación de una habitación del hotel.
 */
public record RoomDTO(
        Long id,
        String number,
        RoomType type,
        String typeLabel,
        int floor,
        int capacity,
        BigDecimal basePrice,
        String description,
        RoomStatus status,
        String statusLabel) {
}
