package com.hotel.reservations.model;

import java.math.BigDecimal;

/**
 * Temporada tarifaria aplicable a una noche de hospedaje.
 */
public enum Season {

    /** Temporada alta (junio, julio, diciembre y enero). */
    HIGH("Temporada alta", new BigDecimal("1.5")),

    /** Temporada baja (resto del año). */
    LOW("Temporada baja", new BigDecimal("1.0"));

    private final String label;
    private final BigDecimal multiplier;

    Season(String label, BigDecimal multiplier) {
        this.label = label;
        this.multiplier = multiplier;
    }

    /** @return etiqueta en español para mostrar al usuario */
    public String getLabel() {
        return label;
    }

    /** @return factor que se aplica sobre la tarifa base */
    public BigDecimal getMultiplier() {
        return multiplier;
    }
}
