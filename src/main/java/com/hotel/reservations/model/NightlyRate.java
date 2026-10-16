package com.hotel.reservations.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tarifa de una noche concreta, con la temporada que le corresponde.
 */
public class NightlyRate {

    private final LocalDate date;
    private final Season season;
    private final BigDecimal price;

    /**
     * @param date fecha de la noche
     * @param season temporada aplicada a esa noche
     * @param price precio de la noche
     */
    public NightlyRate(LocalDate date, Season season, BigDecimal price) {
        this.date = date;
        this.season = season;
        this.price = price;
    }

    /** @return fecha de la noche */
    public LocalDate getDate() {
        return date;
    }

    /** @return temporada de la noche */
    public Season getSeason() {
        return season;
    }

    /** @return precio de la noche */
    public BigDecimal getPrice() {
        return price;
    }
}
