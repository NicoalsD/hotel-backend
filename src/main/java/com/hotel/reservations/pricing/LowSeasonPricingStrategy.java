package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia de temporada baja: el precio por noche es igual a la tarifa base.
 */
@Component
public class LowSeasonPricingStrategy implements PricingStrategy {

    /** @return {@link Season#LOW} */
    @Override
    public Season getSeason() {
        return Season.LOW;
    }

    /**
     * @param basePrice tarifa base por noche
     * @return tarifa base x 1.0, con escala 2
     */
    @Override
    public BigDecimal calculateNightlyPrice(BigDecimal basePrice) {
        return basePrice.multiply(Season.LOW.getMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }
}
