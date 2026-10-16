package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LowSeasonPricingStrategy implements PricingStrategy {

    @Override
    public Season getSeason() {
        return Season.LOW;
    }

    @Override
    public BigDecimal calculateNightlyPrice(BigDecimal basePrice) {
        return basePrice.multiply(Season.LOW.getMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }
}
