package com.hotel.reservations.model;

/**
 * Forma en que se cobra un servicio adicional.
 */
public enum ChargeMode {

    /** Cobro único: cantidad 1. */
    FLAT,

    /** Cobro por noche: cantidad igual al número de noches de la reserva. */
    PER_NIGHT
}
