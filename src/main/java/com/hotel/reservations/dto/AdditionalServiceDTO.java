package com.hotel.reservations.dto;

import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio adicional contratado dentro de una reserva.
 */
public record AdditionalServiceDTO(
        String id,
        AdditionalServiceType type,
        String label,
        ChargeMode chargeMode,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal total,
        LocalDateTime addedAt) {
}
