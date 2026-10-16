package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;

import java.math.BigDecimal;

/**
 * Estrategia de tarificación por temporada (patrón Strategy). Cada implementación
 * define cómo se calcula el precio de una noche a partir de la tarifa base de la habitación.
 */
public interface PricingStrategy {

    /** @return temporada a la que aplica esta estrategia */
    Season getSeason();

    /**
     * @param basePrice tarifa base por noche de la habitación
     * @return precio de una noche en esta temporada, con escala 2
     */
    BigDecimal calculateNightlyPrice(BigDecimal basePrice);
}
