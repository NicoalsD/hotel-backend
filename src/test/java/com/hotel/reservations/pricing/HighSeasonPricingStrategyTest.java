package com.hotel.reservations.pricing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class HighSeasonPricingStrategyTest {

    private final HighSeasonPricingStrategy strategy = new HighSeasonPricingStrategy();

    @Test
    void multipliesBasePriceByOnePointFive() {
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("80.00"))).isEqualTo(new BigDecimal("120.00"));
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("120.00"))).isEqualTo(new BigDecimal("180.00"));
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("250.00"))).isEqualTo(new BigDecimal("375.00"));
    }
}
