package com.hotel.reservations.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceQuoteTest {

    private final DateRange range = new DateRange(LocalDate.of(2026, 12, 30), LocalDate.of(2027, 1, 2));

    private PriceQuote mixedQuote() {
        return new PriceQuote(RoomType.DOUBLE, range, List.of(
                new NightlyRate(LocalDate.of(2026, 12, 30), Season.HIGH, new BigDecimal("180.00")),
                new NightlyRate(LocalDate.of(2026, 12, 31), Season.HIGH, new BigDecimal("180.00")),
                new NightlyRate(LocalDate.of(2027, 1, 1), Season.HIGH, new BigDecimal("180.00"))));
    }

    @Test
    void totalSumsAllNightsWithScaleTwo() {
        PriceQuote quote = mixedQuote();

        assertThat(quote.getTotal()).isEqualTo(new BigDecimal("540.00"));
        assertThat(quote.getNights()).isEqualTo(3);
    }

    @Test
    void countsAndSumsNightsBySeason() {
        PriceQuote quote = new PriceQuote(RoomType.SINGLE, new DateRange(LocalDate.of(2026, 6, 30),
                LocalDate.of(2026, 7, 3)), List.of(
                new NightlyRate(LocalDate.of(2026, 6, 30), Season.HIGH, new BigDecimal("120.00")),
                new NightlyRate(LocalDate.of(2026, 7, 1), Season.HIGH, new BigDecimal("120.00")),
                new NightlyRate(LocalDate.of(2026, 7, 2), Season.LOW, new BigDecimal("80.00"))));

        assertThat(quote.countNights(Season.HIGH)).isEqualTo(2);
        assertThat(quote.countNights(Season.LOW)).isEqualTo(1);
        assertThat(quote.getSubtotalFor(Season.HIGH)).isEqualTo(new BigDecimal("240.00"));
        assertThat(quote.getSubtotalFor(Season.LOW)).isEqualTo(new BigDecimal("80.00"));
        assertThat(quote.getTotal()).isEqualTo(new BigDecimal("320.00"));
    }

    @Test
    void subtotalForSeasonWithoutNightsIsZeroWithScaleTwo() {
        assertThat(mixedQuote().getSubtotalFor(Season.LOW)).isEqualTo(new BigDecimal("0.00"));
        assertThat(mixedQuote().countNights(Season.LOW)).isZero();
    }

    @Test
    void exposesRoomTypeAndRange() {
        PriceQuote quote = mixedQuote();

        assertThat(quote.getRoomType()).isEqualTo(RoomType.DOUBLE);
        assertThat(quote.getRange()).isEqualTo(range);
    }

    @Test
    void nightlyRatesViewCannotBeModified() {
        PriceQuote quote = mixedQuote();

        assertThatThrownBy(() -> quote.getNightlyRates().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
