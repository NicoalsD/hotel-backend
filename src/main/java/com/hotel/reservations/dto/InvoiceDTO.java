package com.hotel.reservations.dto;

import com.hotel.reservations.model.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Factura emitida al hacer check-out de una reserva.
 */
public record InvoiceDTO(
        String invoiceNumber,
        String reservationId,
        LocalDateTime issuedAt,
        GuestDTO guest,
        String roomNumber,
        RoomType roomType,
        String roomTypeLabel,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        List<InvoiceLineDTO> lines,
        BigDecimal roomSubtotal,
        BigDecimal servicesSubtotal,
        BigDecimal subtotal,
        BigDecimal taxRate,
        BigDecimal taxAmount,
        BigDecimal total,
        String digitalKeyCode) {
}
