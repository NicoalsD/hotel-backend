package com.hotel.reservations.exception;

/**
 * Se lanza cuando un rango de fechas es nulo o no es válido (salida no posterior a la entrada).
 */
public class InvalidDateRangeException extends HotelException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message mensaje en español describiendo el problema con las fechas
     */
    public InvalidDateRangeException(String message) {
        super(message);
    }

    /** @return 400 */
    @Override
    public int getStatusCode() {
        return 400;
    }
}
