package com.hotel.reservations.mapper;

import com.hotel.reservations.dto.NightlyRateDTO;
import com.hotel.reservations.dto.PriceQuoteDTO;
import com.hotel.reservations.model.NightlyRate;
import com.hotel.reservations.model.PriceQuote;
import com.hotel.reservations.model.Season;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte cotizaciones de precio del dominio a su representación DTO.
 */
@Component
public class PriceQuoteMapper {

    /**
     * @param quote cotización del dominio
     * @return DTO con el desglose por noche y el conteo por temporada
     */
    public PriceQuoteDTO toDto(PriceQuote quote) {
        List<NightlyRateDTO> rates = quote.getNightlyRates().stream()
                .map(this::toNightlyRateDto)
                .toList();
        return new PriceQuoteDTO(
                quote.getRoomType(),
                quote.getRoomType().getLabel(),
                quote.getRange().getCheckIn(),
                quote.getRange().getCheckOut(),
                quote.getNights(),
                quote.getRoomType().getBasePrice(),
                quote.countNights(Season.HIGH),
                quote.countNights(Season.LOW),
                quote.getTotal(),
                rates);
    }

    private NightlyRateDTO toNightlyRateDto(NightlyRate rate) {
        return new NightlyRateDTO(
                rate.getDate(),
                rate.getSeason(),
                rate.getSeason().getLabel(),
                rate.getPrice());
    }
}
