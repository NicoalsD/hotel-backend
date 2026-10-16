package com.hotel.reservations.exception;

/**
 * Se lanza cuando una operación no es válida para el estado actual de una reserva.
 */
public class InvalidReservationStateException extends HotelException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message mensaje en español describiendo la transición inválida
     */
    public InvalidReservationStateException(String message) {
        super(message);
    }

    /** @return 409 */
    @Override
    public int getStatusCode() {
        return 409;
    }
}
