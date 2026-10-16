package com.hotel.reservations.exception;

import com.hotel.reservations.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private static final String PATH = "/api/hotel/reservar";

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest request(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

    @Test
    void habitacionNoDisponibleResponde409ConCuerpoDeError() {
        RoomNotAvailableException ex = new RoomNotAvailableException(
                "La habitación 201 no está disponible en las fechas seleccionadas");

        ResponseEntity<ErrorResponseDTO> response = handler.handleHotelException(ex, request(PATH));

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        ErrorResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.error()).isEqualTo("Conflict");
        assertThat(body.message()).isEqualTo("La habitación 201 no está disponible en las fechas seleccionadas");
        assertThat(body.path()).isEqualTo(PATH);
        assertThat(body.timestamp()).isNotNull();
    }

    @Test
    void recursoNoEncontradoResponde404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("No se encontró la reserva RES-9999");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleHotelException(ex, request("/api/hotel/reserva/RES-9999"));

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("No se encontró la reserva RES-9999");
        assertThat(response.getBody().path()).isEqualTo("/api/hotel/reserva/RES-9999");
    }

    @Test
    void estadoInvalidoYRangoDeFechasRespondenConSuCodigo() {
        ResponseEntity<ErrorResponseDTO> state = handler.handleHotelException(
                new InvalidReservationStateException("La reserva RES-1001 no puede hacer check-out"),
                request(PATH));
        ResponseEntity<ErrorResponseDTO> dates = handler.handleHotelException(
                new InvalidDateRangeException("La fecha de salida debe ser posterior a la de llegada"),
                request(PATH));

        assertThat(state.getStatusCode().value()).isEqualTo(409);
        assertThat(dates.getStatusCode().value()).isEqualTo(400);
        assertThat(dates.getBody().error()).isEqualTo("Bad Request");
    }

    private MethodArgumentNotValidException validationError(FieldError... errors) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        for (FieldError error : errors) {
            bindingResult.addError(error);
        }
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        return ex;
    }

    @Test
    void erroresDeValidacionSeUnenEnEspanolEnOrdenDeterminista() {
        MethodArgumentNotValidException ex = validationError(
                new FieldError("request", "roomId", "El identificador de la habitación es obligatorio"),
                new FieldError("request", "guest.email", "El correo electrónico no es válido"));

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex, request(PATH));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo(
                "El correo electrónico no es válido; El identificador de la habitación es obligatorio");
    }

    @Test
    void validacionSinMensajesUsaMensajePorDefecto() {
        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(validationError(), request(PATH));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Los datos enviados no son válidos");
    }
}
