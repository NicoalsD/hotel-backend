package com.hotel.reservations.dto;

import com.hotel.reservations.model.AdditionalServiceType;
import com.hotel.reservations.model.ChargeMode;

import java.math.BigDecimal;

/**
 * Elemento del catálogo de servicios adicionales.
 */
public record ServiceCatalogItemDTO(
        AdditionalServiceType type,
        String label,
        BigDecimal unitPrice,
        ChargeMode chargeMode,
        String description) {
}
