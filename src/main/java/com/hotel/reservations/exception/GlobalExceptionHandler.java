package com.hotel.reservations.exception;

import com.hotel.reservations.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP con cuerpo {@link ErrorResponseDTO}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param ex excepción de dominio con su código HTTP
     * @param request solicitud que produjo el error
     * @return respuesta con el código de la excepción
     */
    @ExceptionHandler(HotelException.class)
    public ResponseEntity<ErrorResponseDTO> handleHotelException(HotelException ex, HttpServletRequest request) {
        return build(ex.getStatusCode(), ex.getMessage(), request);
    }

    /**
     * Construye la respuesta de error estándar.
     *
     * @param status código HTTP
     * @param message mensaje en español
     * @param request solicitud original, para obtener la ruta
     * @return respuesta con el cuerpo de error
     */
    protected ResponseEntity<ErrorResponseDTO> build(int status, String message, HttpServletRequest request) {
        HttpStatus resolved = HttpStatus.resolve(status);
        String error = resolved != null ? resolved.getReasonPhrase() : "Error";
        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(), status, error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
