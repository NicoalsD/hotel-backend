package com.hotel.reservations.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos del huésped que realiza o consulta una reserva.
 */
public record GuestDTO(
        @NotBlank(message = "El nombre completo es obligatorio") String fullName,
        @NotBlank(message = "El número de documento es obligatorio") String documentNumber,
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico no es válido") String email,
        @NotBlank(message = "El teléfono es obligatorio") String phone) {
}
