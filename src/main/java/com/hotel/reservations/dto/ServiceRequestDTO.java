package com.hotel.reservations.dto;

import com.hotel.reservations.model.AdditionalServiceType;
import jakarta.validation.constraints.NotNull;

/**
 * Solicitud para agregar un servicio adicional a una reserva.
 */
public record ServiceRequestDTO(
        @NotNull(message = "El tipo de servicio es obligatorio") AdditionalServiceType serviceType) {
}
