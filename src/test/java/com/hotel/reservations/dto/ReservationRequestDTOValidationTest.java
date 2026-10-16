package com.hotel.reservations.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationRequestDTOValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final GuestDTO VALID_GUEST =
            new GuestDTO("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");

    private Set<String> messagesFor(ReservationRequestDTO request) {
        return VALIDATOR.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    @Test
    void solicitudCompletaEsValida() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                7L, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4), VALID_GUEST);

        assertThat(VALIDATOR.validate(request)).isEmpty();
    }

    @Test
    void faltanHabitacionYFechasReportanMensajes() {
        ReservationRequestDTO request = new ReservationRequestDTO(null, null, null, VALID_GUEST);

        assertThat(messagesFor(request)).containsExactlyInAnyOrder(
                "El identificador de la habitación es obligatorio",
                "La fecha de llegada es obligatoria",
                "La fecha de salida es obligatoria");
    }

    @Test
    void huespedAusenteEsRechazado() {
        ReservationRequestDTO request = new ReservationRequestDTO(
                7L, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4), null);

        assertThat(messagesFor(request)).containsExactly("Los datos del huésped son obligatorios");
    }

    @Test
    void huespedInvalidoSeValidaEnCascada() {
        GuestDTO invalidGuest = new GuestDTO("", "1020304050", "ana@correo.com", "3001234567");
        ReservationRequestDTO request = new ReservationRequestDTO(
                7L, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4), invalidGuest);

        assertThat(messagesFor(request)).containsExactly("El nombre completo es obligatorio");
    }
}
