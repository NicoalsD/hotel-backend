package com.hotel.reservations.pricing;

import com.hotel.reservations.model.Season;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PricingStrategyResolverTest {

    private final HighSeasonPricingStrategy high = new HighSeasonPricingStrategy();
    private final LowSeasonPricingStrategy low = new LowSeasonPricingStrategy();
    private final PricingStrategyResolver resolver = new PricingStrategyResolver(List.of(high, low));

    @Test
    void resolvesStrategyForEachSeason() {
        assertThat(resolver.resolve(Season.HIGH)).isSameAs(high);
        assertThat(resolver.resolve(Season.LOW)).isSameAs(low);
    }

    @Test
    void resolvesRegardlessOfInjectionOrder() {
        PricingStrategyResolver reversed = new PricingStrategyResolver(List.of(low, high));

        assertThat(reversed.resolve(Season.HIGH)).isSameAs(high);
        assertThat(reversed.resolve(Season.LOW)).isSameAs(low);
    }
}
