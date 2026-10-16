package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia de temporada alta: el precio por noche es la tarifa base multiplicada por 1.5.
 */
@Component
public class HighSeasonPricingStrategy implements PricingStrategy {

    /** @return {@link Season#HIGH} */
    @Override
    public Season getSeason() {
        return Season.HIGH;
    }

    /**
     * @param basePrice tarifa base por noche
     * @return tarifa base x 1.5, con escala 2
     */
    @Override
    public BigDecimal calculateNightlyPrice(BigDecimal basePrice) {
        return basePrice.multiply(Season.HIGH.getMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }
}
