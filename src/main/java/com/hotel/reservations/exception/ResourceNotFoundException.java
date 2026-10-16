package com.hotel.reservations.exception;

/**
 * Se lanza cuando un recurso solicitado (habitación, reserva o factura) no existe.
 */
public class ResourceNotFoundException extends HotelException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message mensaje en español indicando el recurso no encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /** @return 404 */
    @Override
    public int getStatusCode() {
        return 404;
    }
}
