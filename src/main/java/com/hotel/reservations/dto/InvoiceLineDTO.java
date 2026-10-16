package com.hotel.reservations.dto;

import com.hotel.reservations.model.InvoiceLineCategory;

import java.math.BigDecimal;

/**
 * Línea de una factura, ya sea de hospedaje o de servicios.
 */
public record InvoiceLineDTO(
        String description,
        InvoiceLineCategory category,
        long quantity,
        BigDecimal unitPrice,
        BigDecimal amount) {
}
