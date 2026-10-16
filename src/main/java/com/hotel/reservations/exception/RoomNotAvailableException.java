package com.hotel.reservations.exception;

/**
 * Se lanza cuando una habitación ya está reservada u ocupada en las fechas solicitadas.
 */
public class RoomNotAvailableException extends HotelException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message mensaje en español indicando la habitación no disponible
     */
    public RoomNotAvailableException(String message) {
        super(message);
    }

    /** @return 409 */
    @Override
    public int getStatusCode() {
        return 409;
    }
}
