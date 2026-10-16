package com.hotel.reservations.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cotización de una estancia: detalle noche a noche y total calculado.
 */
public class PriceQuote {

    private final RoomType roomType;
    private final DateRange range;
    private final List<NightlyRate> nightlyRates;

    /**
     * @param roomType tipo de habitación cotizada
     * @param range rango de fechas cotizado
     * @param nightlyRates tarifa de cada noche del rango
     */
    public PriceQuote(RoomType roomType, DateRange range, List<NightlyRate> nightlyRates) {
        this.roomType = roomType;
        this.range = range;
        this.nightlyRates = new ArrayList<>(nightlyRates);
    }

    /** @return tipo de habitación */
    public RoomType getRoomType() {
        return roomType;
    }

    /** @return rango de fechas */
    public DateRange getRange() {
        return range;
    }

    /** @return tarifas por noche, no modificables */
    public List<NightlyRate> getNightlyRates() {
        return Collections.unmodifiableList(nightlyRates);
    }

    /** @return número de noches de la cotización */
    public long getNights() {
        return nightlyRates.size();
    }

    /**
     * @param season temporada a contar
     * @return cantidad de noches que caen en la temporada indicada
     */
    public long countNights(Season season) {
        return nightlyRates.stream().filter(rate -> rate.getSeason() == season).count();
    }

    /** @return suma de todas las noches, con escala 2 */
    public BigDecimal getTotal() {
        return sumPrices(nightlyRates).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @param season temporada a sumar
     * @return suma de las noches de esa temporada, con escala 2
     */
    public BigDecimal getSubtotalFor(Season season) {
        List<NightlyRate> filtered = new ArrayList<>();
        for (NightlyRate rate : nightlyRates) {
            if (rate.getSeason() == season) {
                filtered.add(rate);
            }
        }
        return sumPrices(filtered).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal sumPrices(List<NightlyRate> rates) {
        BigDecimal sum = BigDecimal.ZERO;
        for (NightlyRate rate : rates) {
            sum = sum.add(rate.getPrice());
        }
        return sum;
    }
}
