package com.hotel.reservations.dto;

/**
 * Datos del huésped que realiza o consulta una reserva.
 */
public record GuestDTO(String fullName, String documentNumber, String email, String phone) {
}
