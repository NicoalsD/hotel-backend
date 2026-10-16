package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;

import java.math.BigDecimal;

public interface PricingStrategy {

    Season getSeason();

    BigDecimal calculateNightlyPrice(BigDecimal basePrice);
}
