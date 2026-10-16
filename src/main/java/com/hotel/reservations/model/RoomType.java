package com.hotel.reservations.model;

import java.math.BigDecimal;

/**
 * Tipos de habitación disponibles en el hotel, con su tarifa base y capacidad.
 */
public enum RoomType {

    /** Habitación sencilla para una persona. */
    SINGLE("Sencilla", new BigDecimal("80.00"), 1),

    /** Habitación doble para dos personas. */
    DOUBLE("Doble", new BigDecimal("120.00"), 2),

    /** Suite para hasta cuatro personas. */
    SUITE("Suite", new BigDecimal("250.00"), 4);

    private final String label;
    private final BigDecimal basePrice;
    private final int capacity;

    RoomType(String label, BigDecimal basePrice, int capacity) {
        this.label = label;
        this.basePrice = basePrice;
        this.capacity = capacity;
    }

    /** @return etiqueta en español para mostrar al usuario */
    public String getLabel() {
        return label;
    }

    /** @return tarifa base por noche antes de aplicar la temporada */
    public BigDecimal getBasePrice() {
        return basePrice;
    }

    /** @return número máximo de huéspedes */
    public int getCapacity() {
        return capacity;
    }
}
