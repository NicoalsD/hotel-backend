package com.hotel.reservations.dto;

import com.hotel.reservations.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representación completa de una reserva.
 */
public record ReservationDTO(
        String id,
        ReservationStatus status,
        String statusLabel,
        GuestDTO guest,
        RoomDTO room,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        BigDecimal roomTotal,
        List<AdditionalServiceDTO> services,
        BigDecimal servicesTotal,
        BigDecimal estimatedTotal,
        DigitalKeyDTO digitalKey,
        String invoiceNumber,
        LocalDateTime createdAt,
        LocalDateTime checkedInAt,
        LocalDateTime checkedOutAt) {
}
