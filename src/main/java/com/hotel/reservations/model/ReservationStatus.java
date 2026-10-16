package com.hotel.reservations.model;

/**
 * Estados del ciclo de vida de una reserva.
 */
public enum ReservationStatus {

    /** Reserva creada, aún sin check-in. */
    CONFIRMED("Confirmada"),

    /** El huésped realizó el check-in y ocupa la habitación. */
    CHECKED_IN("Check-in realizado"),

    /** El huésped realizó el check-out y la reserva fue facturada. */
    CHECKED_OUT("Finalizada");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    /** @return etiqueta en español para mostrar al usuario */
    public String getLabel() {
        return label;
    }
}
