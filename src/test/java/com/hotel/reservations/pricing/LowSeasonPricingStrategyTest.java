package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LowSeasonPricingStrategyTest {

    private final LowSeasonPricingStrategy strategy = new LowSeasonPricingStrategy();

    @Test
    void keepsBasePriceUnchanged() {
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("80.00"))).isEqualTo(new BigDecimal("80.00"));
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("120.00"))).isEqualTo(new BigDecimal("120.00"));
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("250.00"))).isEqualTo(new BigDecimal("250.00"));
    }

    @Test
    void reportsLowSeason() {
        assertThat(strategy.getSeason()).isEqualTo(Season.LOW);
    }

    @Test
    void resultHasScaleTwo() {
        assertThat(strategy.calculateNightlyPrice(new BigDecimal("80")).scale()).isEqualTo(2);
    }
}
