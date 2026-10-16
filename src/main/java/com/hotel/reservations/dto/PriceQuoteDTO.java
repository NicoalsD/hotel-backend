package com.hotel.reservations.dto;

import com.hotel.reservations.model.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista previa del precio de una habitación para un rango de fechas.
 */
public record PriceQuoteDTO(
        RoomType roomType,
        String typeLabel,
        LocalDate checkIn,
        LocalDate checkOut,
        long nights,
        BigDecimal basePrice,
        long highSeasonNights,
        long lowSeasonNights,
        BigDecimal total,
        List<NightlyRateDTO> nightlyRates) {
}
