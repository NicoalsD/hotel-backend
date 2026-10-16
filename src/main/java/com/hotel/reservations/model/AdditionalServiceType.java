package com.hotel.reservations.model;

import java.math.BigDecimal;

/**
 * Catálogo de servicios adicionales que el huésped puede contratar.
 */
public enum AdditionalServiceType {

    /** Sesión de spa y relajación. */
    SPA("Spa", new BigDecimal("50.00"), ChargeMode.FLAT, "Sesión de spa y relajación"),

    /** Desayuno incluido por cada noche. */
    BREAKFAST("Desayuno", new BigDecimal("15.00"), ChargeMode.PER_NIGHT, "Desayuno buffet por persona y noche"),

    /** Traslado desde o hacia el aeropuerto. */
    TRANSFER("Traslado", new BigDecimal("30.00"), ChargeMode.FLAT, "Traslado en vehículo privado al aeropuerto");

    private final String label;
    private final BigDecimal unitPrice;
    private final ChargeMode chargeMode;
    private final String description;

    AdditionalServiceType(String label, BigDecimal unitPrice, ChargeMode chargeMode, String description) {
        this.label = label;
        this.unitPrice = unitPrice;
        this.chargeMode = chargeMode;
        this.description = description;
    }

    /** @return etiqueta en español para mostrar al usuario */
    public String getLabel() {
        return label;
    }

    /** @return precio unitario del servicio */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /** @return forma de cobro del servicio */
    public ChargeMode getChargeMode() {
        return chargeMode;
    }

    /** @return descripción en español del servicio */
    public String getDescription() {
        return description;
    }
}
