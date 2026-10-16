package com.hotel.reservations.service;

import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.model.Season;
import com.hotel.reservations.pricing.HighSeasonPricingStrategy;
import com.hotel.reservations.pricing.LowSeasonPricingStrategy;
import com.hotel.reservations.pricing.PricingStrategyResolver;
import com.hotel.reservations.pricing.SeasonCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateServiceTest {

    private RateService rateService;

    @BeforeEach
    void setUp() {
        PricingStrategyResolver resolver = new PricingStrategyResolver(
                List.of(new HighSeasonPricingStrategy(), new LowSeasonPricingStrategy()));
        rateService = new RateService(new SeasonCalendar(), resolver);
    }

    @Test
    void lowSeasonStayUsesBasePricePerRoomType() {
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4));

        assertThat(rateService.calculateTotal(RoomType.SINGLE, range)).isEqualTo(new BigDecimal("240.00"));
        assertThat(rateService.calculateTotal(RoomType.DOUBLE, range)).isEqualTo(new BigDecimal("360.00"));
        assertThat(rateService.calculateTotal(RoomType.SUITE, range)).isEqualTo(new BigDecimal("750.00"));
    }

    @Test
    void highSeasonStayUsesIncreasedPricePerRoomType() {
        DateRange range = new DateRange(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 4));

        assertThat(rateService.calculateTotal(RoomType.SINGLE, range)).isEqualTo(new BigDecimal("360.00"));
        assertThat(rateService.calculateTotal(RoomType.DOUBLE, range)).isEqualTo(new BigDecimal("540.00"));
        assertThat(rateService.calculateTotal(RoomType.SUITE, range)).isEqualTo(new BigDecimal("1125.00"));
    }

    @Test
    void stayCrossingNovemberThirtyToDecemberFirstMixesSeasons() {
        DateRange range = new DateRange(LocalDate.of(2026, 11, 29), LocalDate.of(2026, 12, 2));
        PriceQuote quote = rateService.calculateQuote(RoomType.DOUBLE, range);

        assertThat(quote.getNights()).isEqualTo(3);
        assertThat(quote.countNights(Season.LOW)).isEqualTo(2);
        assertThat(quote.countNights(Season.HIGH)).isEqualTo(1);
        assertThat(quote.getTotal()).isEqualTo(new BigDecimal("420.00"));
        assertThat(rateService.calculateTotal(RoomType.SINGLE, range)).isEqualTo(new BigDecimal("280.00"));
        assertThat(rateService.calculateTotal(RoomType.SUITE, range)).isEqualTo(new BigDecimal("875.00"));
    }

    @Test
    void quoteDetailsEachNightWithItsSeasonAndPrice() {
        DateRange range = new DateRange(LocalDate.of(2026, 11, 30), LocalDate.of(2026, 12, 2));
        PriceQuote quote = rateService.calculateQuote(RoomType.DOUBLE, range);

        assertThat(quote.getNightlyRates()).hasSize(2);
        assertThat(quote.getNightlyRates().get(0).getDate()).isEqualTo(LocalDate.of(2026, 11, 30));
        assertThat(quote.getNightlyRates().get(0).getSeason()).isEqualTo(Season.LOW);
        assertThat(quote.getNightlyRates().get(0).getPrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(quote.getNightlyRates().get(1).getDate()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(quote.getNightlyRates().get(1).getSeason()).isEqualTo(Season.HIGH);
        assertThat(quote.getNightlyRates().get(1).getPrice()).isEqualTo(new BigDecimal("180.00"));
    }

    @Test
    void resolvesSeasonOfSingleDate() {
        assertThat(rateService.resolveSeason(LocalDate.of(2026, 12, 1))).isEqualTo(Season.HIGH);
        assertThat(rateService.resolveSeason(LocalDate.of(2026, 11, 30))).isEqualTo(Season.LOW);
    }

    @Test
    void rejectsMissingRoomTypeOrRange() {
        DateRange range = new DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 2));

        assertThatThrownBy(() -> rateService.calculateQuote(null, range)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> rateService.calculateQuote(RoomType.SINGLE, null))
                .isInstanceOf(NullPointerException.class);
    }
}
