package com.hotel.reservations.model;

/**
 * Estado físico de una habitación.
 */
public enum RoomStatus {

    /** La habitación está libre para ser ocupada. */
    AVAILABLE("Disponible"),

    /** La habitación está ocupada por un huésped con check-in realizado. */
    OCCUPIED("Ocupada");

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    /** @return etiqueta en español para mostrar al usuario */
    public String getLabel() {
        return label;
    }
}
