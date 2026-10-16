package com.hotel.reservations.dto;

import com.hotel.reservations.model.Season;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tarifa de una noche concreta dentro de una cotización.
 */
public record NightlyRateDTO(LocalDate date, Season season, String seasonLabel, BigDecimal price) {
}
