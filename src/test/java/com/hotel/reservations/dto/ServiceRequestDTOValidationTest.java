package com.hotel.reservations.dto;

import com.hotel.reservations.model.AdditionalServiceType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceRequestDTOValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void tipoDeServicioPresenteEsValido() {
        assertThat(VALIDATOR.validate(new ServiceRequestDTO(AdditionalServiceType.SPA))).isEmpty();
    }

    @Test
    void tipoDeServicioAusenteEsRechazadoConMensajeEnEspanol() {
        assertThat(VALIDATOR.validate(new ServiceRequestDTO(null)))
                .extracting(violation -> violation.getMessage())
                .containsExactly("El tipo de servicio es obligatorio");
    }
}
