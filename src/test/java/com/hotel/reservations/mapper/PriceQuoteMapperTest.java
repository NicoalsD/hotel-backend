package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.NightlyRateDTO;
import com.hotel.reservations.dto.PriceQuoteDTO;
import com.hotel.reservations.model.DateRange;
import com.hotel.reservations.model.NightlyRate;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.RoomType;
import com.hotel.reservations.model.Season;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PriceQuoteMapperTest {

    private final PriceQuoteMapper mapper = new PriceQuoteMapper();

    @Test
    void convierteCotizacionDeTemporadaAltaConDesglose() {
        DateRange range = new DateRange(LocalDate.of(2026, 12, 30), LocalDate.of(2027, 1, 2));
        PriceQuote quote = new PriceQuote(RoomType.DOUBLE, range, List.of(
                new NightlyRate(LocalDate.of(2026, 12, 30), Season.HIGH, new BigDecimal("180.00")),
                new NightlyRate(LocalDate.of(2026, 12, 31), Season.HIGH, new BigDecimal("180.00")),
                new NightlyRate(LocalDate.of(2027, 1, 1), Season.HIGH, new BigDecimal("180.00"))));

        PriceQuoteDTO dto = mapper.toDto(quote);

        assertThat(dto.roomType()).isEqualTo(RoomType.DOUBLE);
        assertThat(dto.typeLabel()).isEqualTo("Doble");
        assertThat(dto.checkIn()).isEqualTo(LocalDate.of(2026, 12, 30));
        assertThat(dto.checkOut()).isEqualTo(LocalDate.of(2027, 1, 2));
        assertThat(dto.nights()).isEqualTo(3);
        assertThat(dto.basePrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(dto.highSeasonNights()).isEqualTo(3);
        assertThat(dto.lowSeasonNights()).isZero();
        assertThat(dto.total()).isEqualTo(new BigDecimal("540.00"));
        assertThat(dto.nightlyRates()).hasSize(3);
        assertThat(dto.nightlyRates().get(0)).isEqualTo(new NightlyRateDTO(
                LocalDate.of(2026, 12, 30), Season.HIGH, "Temporada alta", new BigDecimal("180.00")));
    }

    @Test
    void contaNochesPorTemporadaEnCotizacionMixta() {
        DateRange range = new DateRange(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 7, 3));
        PriceQuote quote = new PriceQuote(RoomType.SINGLE, range, List.of(
                new NightlyRate(LocalDate.of(2026, 6, 30), Season.HIGH, new BigDecimal("120.00")),
                new NightlyRate(LocalDate.of(2026, 7, 1), Season.HIGH, new BigDecimal("120.00")),
                new NightlyRate(LocalDate.of(2026, 7, 2), Season.LOW, new BigDecimal("80.00"))));

        PriceQuoteDTO dto = mapper.toDto(quote);

        assertThat(dto.highSeasonNights()).isEqualTo(2);
        assertThat(dto.lowSeasonNights()).isEqualTo(1);
        assertThat(dto.total()).isEqualTo(new BigDecimal("320.00"));
        assertThat(dto.nightlyRates().get(2).seasonLabel()).isEqualTo("Temporada baja");
        assertThat(dto.nightlyRates().get(2).price()).isEqualTo(new BigDecimal("80.00"));
    }
}
