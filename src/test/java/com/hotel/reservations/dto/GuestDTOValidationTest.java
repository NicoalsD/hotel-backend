package com.hotel.reservations.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GuestDTOValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<String> messagesFor(GuestDTO guest) {
        return VALIDATOR.validate(guest).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }

    @Test
    void huespedCompletoEsValido() {
        GuestDTO guest = new GuestDTO("Ana Pérez", "1020304050", "ana@correo.com", "3001234567");

        assertThat(VALIDATOR.validate(guest)).isEmpty();
    }

    @Test
    void camposVaciosReportanMensajesEnEspanol() {
        GuestDTO guest = new GuestDTO(" ", "", null, "  ");

        assertThat(messagesFor(guest)).containsExactlyInAnyOrder(
                "El nombre completo es obligatorio",
                "El número de documento es obligatorio",
                "El correo electrónico es obligatorio",
                "El teléfono es obligatorio");
    }

    @Test
    void correoConFormatoInvalidoEsRechazado() {
        GuestDTO guest = new GuestDTO("Ana Pérez", "1020304050", "no-es-correo", "3001234567");

        assertThat(messagesFor(guest)).containsExactly("El correo electrónico no es válido");
    }
}
