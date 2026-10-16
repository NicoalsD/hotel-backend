package com.hotel.reservations.exception;

/**
 * Excepción base de las reglas de negocio del hotel.
 * Cada subclase indica el código HTTP con el que debe responderse, sin depender de Spring.
 */
public abstract class HotelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message mensaje en español describiendo el error
     */
    protected HotelException(String message) {
        super(message);
    }

    /**
     * @return código de estado HTTP asociado a la excepción
     */
    public abstract int getStatusCode();
}
