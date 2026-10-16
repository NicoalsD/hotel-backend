package com.hotel.reservations.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos del huésped que realiza o consulta una reserva.
 */
public record GuestDTO(
        @NotBlank(message = "El nombre completo es obligatorio") String fullName,
        @NotBlank(message = "El número de documento es obligatorio") String documentNumber,
        @NotBlank(message = "El correo electrónico es obligatorio") String email,
        @NotBlank(message = "El teléfono es obligatorio") String phone) {
}
