package com.hotel.reservations.dto;

import com.hotel.reservations.model.AdditionalServiceType;

/**
 * Solicitud para agregar un servicio adicional a una reserva.
 */
public record ServiceRequestDTO(AdditionalServiceType serviceType) {
}
