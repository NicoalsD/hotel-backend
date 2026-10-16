package com.hotel.reservations.exception;

import com.hotel.reservations.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP con cuerpo {@link ErrorResponseDTO}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String DEFAULT_VALIDATION_MESSAGE = "Los datos enviados no son válidos";

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
     * @param ex errores de validación de un cuerpo de solicitud
     * @param request solicitud que produjo el error
     * @return respuesta 400 con los mensajes de validación unidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = DEFAULT_VALIDATION_MESSAGE;
        }
        return build(HttpStatus.BAD_REQUEST.value(), message, request);
    }

    /**
     * @param ex parámetro obligatorio ausente en la solicitud
     * @param request solicitud que produjo el error
     * @return respuesta 400 indicando el parámetro faltante
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException ex,
                                                                   HttpServletRequest request) {
        String message = "El parámetro obligatorio '" + ex.getParameterName() + "' no fue enviado";
        return build(HttpStatus.BAD_REQUEST.value(), message, request);
    }

    /**
     * @param ex parámetro con un tipo de valor no válido
     * @param request solicitud que produjo el error
     * @return respuesta 400 indicando el parámetro inválido
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                               HttpServletRequest request) {
        String message = "El valor '" + ex.getValue() + "' no es válido para el parámetro '" + ex.getName() + "'";
        return build(HttpStatus.BAD_REQUEST.value(), message, request);
    }

    /**
     * @param ex cuerpo de solicitud que no se puede leer o no tiene el formato esperado
     * @param request solicitud que produjo el error
     * @return respuesta 400 con mensaje genérico en español
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableBody(HttpMessageNotReadableException ex,
                                                                 HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST.value(), "El cuerpo de la solicitud no es válido", request);
    }

    /**
     * Respuesta genérica para cualquier error no controlado. El detalle se registra en el log.
     *
     * @param ex error no controlado
     * @param request solicitud que produjo el error
     * @return respuesta 500 con mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOG.error("Error no controlado en {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor", request);
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
